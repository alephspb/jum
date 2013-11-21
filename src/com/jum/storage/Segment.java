package com.rubber.storage;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created with IntelliJ IDEA.
 * User: aleph
 * Date: 20.11.13
 * Time: 23:13
 * To change this template use File | Settings | File Templates.
 */
public class Segment {
    private int capacity;
    byte[] data;
    int offset;
    ByteBuffer byteBuffer;

    public Segment(int capacity) {
        this.capacity = capacity;
        offset = 0;
        data = new byte[capacity];
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

    public static void main(String[] args) {
        int l = 435;
        byte[] data = new byte[] {(byte)(l >>> 24), (byte)(l >>> 16), (byte)(l >>> 8), (byte)l};
        int out = 0;
        out =  data[3] & 0xFF | (data[2] & 0xFF) << 8 | (data[1] & 0xFF) << 16 | (data[0] & 0xFF) << 24;
        System.out.println(out);
        System.out.println(String.format("%02x", data[0]) + "" + String.format("%02x", data[1]) + "" + String.format("%02x", data[2]) + "" + String.format("%02x", data[3]));
        System.out.println(Integer.toHexString(l));
        System.out.println(Integer.toHexString(out));
    }
}
