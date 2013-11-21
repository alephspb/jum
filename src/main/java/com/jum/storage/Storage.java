package com.jum.storage;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;

/**
 * Created with IntelliJ IDEA.
 * User: aleph
 * Date: 20.11.13
 * Time: 22:50
 * To change this template use File | Settings | File Templates.
 */
public class Storage {
    private Segment segment;

    public Storage() {
        segment = new Segment(1048576);
    }

    public int putObject(Object o) throws IOException {
        return segment.put(serialize(o));
    }

    public Object deleteObject(int ref) {
        return null;
    }

    public Object getObject(int ref) throws IOException, ClassNotFoundException {
        return deserialize(segment.get(ref));
    }

    private byte[] serialize(Object o) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(b);
        out.writeObject(o);
        out.close();
        return b.toByteArray();
    }

    private Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream b = new ByteArrayInputStream(data);
        ObjectInputStream in = new ObjectInputStream(b);
        Object o = in.readObject();
        in.close();
        return o;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Storage s = new Storage();

        int it = 23000;

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
//            System.out.println(o);
        }

        for (int i = 0; i < it; i++) {
            Object o = s.getObject(refs1[i]);
//            System.out.println(o);
        }

        System.out.println(s.segment.getOffset());
        System.out.println(System.currentTimeMillis() - l);

    }


}
