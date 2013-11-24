package com.jum.storage;

public interface Segment {

    public void putInt(int data);

    public void put(byte[] o);

    public int getInt();

    public byte[] get(int len);

    public int position();

    public void position(int pos);

    public int getOffset();
}
