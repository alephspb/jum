package com.jum.util;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: aleph
 * Date: 24.11.13
 * Time: 2:57
 * To change this template use File | Settings | File Templates.
 */
public class SerializationHelper {

    public static byte[] serialize(Object o) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(b);
        out.writeObject(o);
        out.close();
        return b.toByteArray();
    }

    public static Object deserialize(byte[] data) throws IOException {
        ByteArrayInputStream b = new ByteArrayInputStream(data);
        ObjectInputStream in = new ObjectInputStream(b);
        Object o = null;
        try {
            o = in.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        } finally {
            in.close();
        }
        return o;
    }

}
