package com.jum.storage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Storage {
    private List<Segment> segments;
    private int segmentSize;

    public Storage() {
        segmentSize = 1048576;
        segments = new ArrayList<Segment>();
        segments.add(new Segment(segmentSize));
    }

    public int putObject(Object o) throws IOException {
        byte[] data = serialize(o);
        int lastSegmentId = segments.size() - 1;
        Segment lastSegment = segments.get(lastSegmentId);
        if (segmentSize - lastSegment.getOffset() < data.length + 4) {
            lastSegment = new Segment(segmentSize);
            segments.add(lastSegment);
            lastSegmentId = segments.size() - 1;
        }

        return lastSegmentId * segmentSize + lastSegment.put(data);
    }

    public Object deleteObject(int ref) {
        return null;
    }

    public Object getObject(int ref) throws IOException, ClassNotFoundException {
        int segmentId = ref / segmentSize;
        Segment segment = segments.get(segmentId);
        return deserialize(segment.get(ref % segmentSize));
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

        int it = 1000000;

        long l = System.currentTimeMillis();

        int[] refs = new int[it];
        int[] refs1 = new int[it];
        for (int i = 0; i < it; i++) {
            String a = "Hello!!!" + i;
            refs[i] = s.putObject(a);
            s.putObject(a);
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

        System.out.println(s.segments.size() + ":" + s.segments.get(s.segments.size() - 1).getOffset());
        System.out.println(System.currentTimeMillis() - l);

    }


}
