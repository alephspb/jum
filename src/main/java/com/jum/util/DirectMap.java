package com.jum.util;

import com.jum.storage.Segment;
import com.jum.storage.Storage;

import java.io.*;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class DirectMap<K,V> implements Map<K,V> {
    private Storage indexStorage;
    private Segment index;
    private Storage storage;
    private int capacity;

    public DirectMap() {
        indexStorage = new Storage();
        storage = new Storage();
        capacity = 128;
        index = indexStorage.allocateSegment(capacity * 4);
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public V get(Object key) {
        int ind = getHash(key.hashCode());
        index.position(ind * 4);
        int ref = index.getInt();
        Segment segment = storage.getSegment(ref);
        int kLen = segment.getInt();
        int vLen = segment.getInt();
//        segment.getInt();
        try {
            segment.position(12 + kLen);
//            Object key1 = SerializationHelper.deserialize(segment.get(kLen));
            V value = (V) SerializationHelper.deserialize(segment.get(vLen));
//            V value = (V) segment.get(vLen);
            return value;
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public V put(Object key, Object value) {
        try {
            int ind = getHash(key.hashCode());
            index.position(ind * 4);
            int ref = index.getInt();
            if (ref <= 0) {
                byte[] keyData = SerializationHelper.serialize(key);
                byte[] valueData = SerializationHelper.serialize(value);
//            byte[] keyData = (byte[]) key;
//            byte[] valueData = (byte[]) value;
                Segment segment = storage.allocateSegment(12 + keyData.length + valueData.length);
                segment.putInt(keyData.length);
                segment.putInt(valueData.length);
                segment.putInt(-1);
                segment.put(keyData);
                segment.put(valueData);

                ref = segment.getOffset();
                index.position(ind * 4);
                index.putInt(ref);
            } else {
                Segment segment = storage.getSegment(ref);
                int kLen = segment.getInt();
                segment.position(8);
                int nextRef = segment.getInt();
                Object key1 = SerializationHelper.deserialize(segment.get(kLen));
                if (!key.equals(key1)) {
                    if (nextRef == -1) {
//                        TODO add entry
                    } else {
//                        TODO check next entry
                    }
                } else {
//                    TODO rewrite entry
                }
            }


        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public V remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map m) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void clear() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Set keySet() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection values() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Set<Entry<K,V>> entrySet() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private int getHash(int code) {
        return Math.abs(code % capacity);
    }



    public static void main(String[] args) {
        DirectMap map = new DirectMap();
        String key = "Some key";
        String value = "Some value";

        map.put("some key1", "some value1");
        map.put(key, value);
        System.out.println(map.get(key));
    }

}
