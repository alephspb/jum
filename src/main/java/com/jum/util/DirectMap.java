package com.jum.util;

import com.jum.storage.Segment;
import com.jum.storage.Storage;

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
        capacity = 1024;
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
        while (ref > 0) {
            Segment segment = storage.getSegment(ref);
            int kLen = segment.getInt();
            int vLen = segment.getInt();
            int nextRef = segment.getInt();
            try {
                segment.position(12);
                Object key1 = SerializationHelper.deserialize(segment.get(kLen));
                if (key.equals(key1)) {
                    V value = (V) SerializationHelper.deserialize(segment.get(vLen));
                    return value;
                } else {
                    ref = nextRef;
                }
            } catch (Exception e) {

            }
        }
        return null;
    }

    @Override
    public V put(Object key, Object value) {
        try {
            byte[] keyData = SerializationHelper.serialize(key);
            byte[] valueData = SerializationHelper.serialize(value);
            Segment segment = storage.allocateSegment(12 + keyData.length + valueData.length);
            segment.putInt(keyData.length);
            segment.putInt(valueData.length);
            segment.putInt(-1);
            segment.put(keyData);
            segment.put(valueData);
            int newRef = segment.getOffset();

            int ind = getHash(key.hashCode());
            index.position(ind * 4);
            int ref = index.getInt();
            if (ref <= 0) {
                index.position(ind * 4);
                index.putInt(newRef);
            } else {
                segment = storage.getSegment(ref);
                while (true) {
                    segment.position(0);
                    int kLen = segment.getInt();
                    segment.position(12);
                    Object key1 = SerializationHelper.deserialize(segment.get(kLen));
                    if (!key.equals(key1)) {
                        segment.position(8);
                        int nextRef = segment.getInt();
                        if (nextRef > -1) {
                            //TODO check next entry
                            segment = storage.getSegment(nextRef);
                        } else {
                            segment.position(8);
                            segment.putInt(newRef);
                            return null;
                        }
                    } else {
//                    TODO rewrite entry
                        return null;
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection values() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Entry<K,V>> entrySet() {
        throw new UnsupportedOperationException();
    }

    private int getHash(int code) {
        return Math.abs(code % capacity);
    }



    public static void main(String[] args) {
        Map<Object, Object> map = new DirectMap<Object, Object>();
        for (int i = 0; i < 10000; i++) {
//            TestKey key1 = new TestKey(i, i);
            String value1 = "Some value" + i;
            map.put(new TestKey(1, i), value1);
            if (i % 100 == 0)
                System.out.println(value1);
        }

        System.out.println(map.get(new TestKey(1, 45)));
    }

}
