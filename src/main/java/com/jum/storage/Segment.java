package com.jum.storage;

import java.nio.ByteBuffer;

public class Segment {
    private int capacity;
    int offset;
    ByteBuffer byteBuffer;

    public Segment(int capacity) {
        this.capacity = capacity;
        offset = 0;
        byteBuffer = ByteBuffer.allocateDirect(capacity);
    }

    public int put(byte[] o) {
        byteBuffer.putInt(o.length);
        byteBuffer.put(o);
        int pOffset = offset;
        offset = byteBuffer.position();
        return pOffset;
    }

    public byte[] get(int i) {
        byteBuffer.position(i);
        int len = byteBuffer.getInt();
        byte[] out = new byte[len];
        byteBuffer.get(out);
        byteBuffer.position(offset);
        return out;
    }

    public int getOffset() {
        return offset;
    }
}
