package com.jum.storage;

import com.jum.util.SerializationHelper;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Storage {
    private List<ByteBuffer> segments;
    private int segmentSize;
    private int offset;

    public Storage() {
        segmentSize = 104857600;
        segments = new ArrayList<ByteBuffer>();
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(segmentSize);
        segments.add(byteBuffer);
        offset = 0;
    }

    public Segment getSegment(int ref) {
        int segmentId = ref / segmentSize;
        ByteBuffer byteBuffer = segments.get(segmentId);
        return new SegmentImpl(ref % segmentSize, -1, byteBuffer);
    }

    public Segment allocateSegment(int len) {
        int lastSegmentId = segments.size() - 1;
        ByteBuffer lastSegment = segments.get(lastSegmentId);
        int remain = segmentSize - lastSegment.position();
        if (remain < len) {
            lastSegment = ByteBuffer.allocateDirect(segmentSize);
            segments.add(lastSegment);
            offset += len + remain;
        }

        return new SegmentImpl(lastSegmentId * segmentSize + lastSegment.position(), len, lastSegment);
    }

    private class SegmentImpl implements Segment {
        private int offset;
        private int bOffset;
        private int size;
        private int pos;
        private ByteBuffer byteBuffer;

        private SegmentImpl(int offset, int size, ByteBuffer byteBuffer) {
            this.offset = offset;
            bOffset = offset % segmentSize;
            this.size = size;
            this.byteBuffer = byteBuffer;
            pos = 0;
        }

        @Override
        public void putInt(int data) {
            Storage.putInt(bOffset + pos, data, byteBuffer);
            pos += 4;
        }

        @Override
        public void put(byte[] o) {
            Storage.put(bOffset + pos, o, byteBuffer);
            pos += o.length;
        }

        @Override
        public int getInt() {
            int out =  Storage.getInt(bOffset + pos, byteBuffer);
            pos += 4;
            return out;
        }

        @Override
        public byte[] get(int len) {
            byte[] out = Storage.get(bOffset + pos, len, byteBuffer);
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
        int pOffset = byteBuffer.position();
        byteBuffer.position(i);
        int out = byteBuffer.getInt();
        byteBuffer.position(pOffset);
        return out;
    }

    private static byte[] get(int i, int len, ByteBuffer byteBuffer) {
        int pOffset = byteBuffer.position();
        byteBuffer.position(i);
        byte[] out = new byte[len];
        byteBuffer.get(out);
        byteBuffer.position(pOffset);
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

        System.out.println(s.segments.size() + ":" + s.segments.get(s.segments.size() - 1).position());
        System.out.println(System.currentTimeMillis() - l);

    }


}
