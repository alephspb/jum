package com.jum.storage;

import com.jum.util.SerializationHelper;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Storage {
    private List<ByteBuffer> chunks;
    private int segmentSize;
    private int offset;

    public Storage() {
        segmentSize = 104857600;
        chunks = new ArrayList<ByteBuffer>();
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(segmentSize);
        byteBuffer.position(1);
        chunks.add(byteBuffer);
        offset = 0;
    }

    public Segment getSegment(int ref) {
        int segmentId = ref / segmentSize;
        ByteBuffer byteBuffer = chunks.get(segmentId);
        return new SegmentImpl(ref % segmentSize, -1, byteBuffer);
    }

    public Segment allocateSegment(int len) {
        int lastSegmentId = chunks.size() - 1;
        ByteBuffer lastSegment = chunks.get(lastSegmentId);
        int remain = segmentSize - (offset % segmentSize);
        if (remain < len) {
            lastSegment = ByteBuffer.allocateDirect(segmentSize);
            chunks.add(lastSegment);
            offset = chunks.size() * segmentSize;
        }
        Segment s = new SegmentImpl(offset, len, lastSegment);
        offset += len;
        return s;
    }

    private class SegmentImpl implements Segment {
        private int offset;
        private int bOffset;
        private int size;
        private int pos;
        private ByteBuffer chunk;

        private SegmentImpl(int offset, int size, ByteBuffer chunk) {
            this.offset = offset;
            bOffset = offset % segmentSize;
            this.size = size;
            this.chunk = chunk;
            pos = 0;
        }

        @Override
        public void putInt(int data) {
            Storage.putInt(bOffset + pos, data, chunk);
            pos += 4;
        }

        @Override
        public void put(byte[] o) {
            Storage.put(bOffset + pos, o, chunk);
            pos += o.length;
        }

        @Override
        public int getInt() {
            int out =  Storage.getInt(bOffset + pos, chunk);
            pos += 4;
            return out;
        }

        @Override
        public byte[] get(int len) {
            byte[] out = Storage.get(bOffset + pos, len, chunk);
            pos += len;
            return out;
        }

        @Override
        public int position() {
            return pos;
        }

        @Override
        public void position(int pos) {
            this.pos = pos;
        }

        public int getOffset() {
            return offset;
        }
    }


    public int putObject(Object o) throws IOException {
        byte[] data = SerializationHelper.serialize(o);
        Segment s = allocateSegment(data.length + 4);

        s.putInt(data.length);
        s.put(data);
        return s.getOffset();
    }

    public Object deleteObject(int ref) {
        return null;
    }

    public Object getObject(int ref) throws IOException, ClassNotFoundException {
        Segment s = getSegment(ref);
        int len = s.getInt();
        return SerializationHelper.deserialize(s.get(len));
    }


    private static int putInt(int i, int data, ByteBuffer byteBuffer) {
        int pOffset = byteBuffer.position();
        byteBuffer.position(i);
        byteBuffer.putInt(data);
        return pOffset;
    }

    private static int put(int i, byte[] o, ByteBuffer byteBuffer) {
        int pOffset = byteBuffer.position();
        byteBuffer.position(i);
        byteBuffer.put(o);
        return pOffset;
    }

    private static int getInt(int i, ByteBuffer byteBuffer) {
        byteBuffer.position(i);
        int out = byteBuffer.getInt();
        return out;
    }

    private static byte[] get(int i, int len, ByteBuffer byteBuffer) {
        byteBuffer.position(i);
        byte[] out = new byte[len];
        byteBuffer.get(out);
        return out;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Storage s = new Storage();

        int it = 10000000;

        long l = System.currentTimeMillis();

        int[] refs = new int[it];
        int[] refs1 = new int[it];
        for (int i = 0; i < it; i++) {
            String a = "Hello!!!" + i;
            refs[i] = s.putObject(a);
        }
        for (int i = 0; i < it; i++) {
            Object o = s.getObject(refs[i]);
            refs1[i] = s.putObject("Second" + i);
            System.out.println(o);
        }

        for (int i = 0; i < it; i++) {
            Object o = s.getObject(refs1[i]);
            System.out.println(o);
        }

        System.out.println(s.chunks.size() + ":" + s.chunks.get(s.chunks.size() - 1).position());
        System.out.println(System.currentTimeMillis() - l);

    }


}
