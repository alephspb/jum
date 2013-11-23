package com.jum.hash;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoubleVsDivisionHash {
    private static String[] indexArray;
    private static int collision = 0;

    public static void main(String[] args) throws IOException {

        InputStream words = Thread.currentThread().getContextClassLoader().getResourceAsStream("words.txt");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(words));
        String line;
        List<String> list = new ArrayList<String>();
        while ((line = bufferedReader.readLine()) != null) {
            list.add(line);
        }
        int mapSize = new PrimeFinder().greaterThen(list.size() * 2);

        System.out.println("Size of input: " + list.size());
        System.out.println("Size of index: " + mapSize);
        System.out.println("++++++++++++++++++++++++");
        testDivisionHashing(list, mapSize);
        System.out.println("++++++++++++++++++++++++");
        testDoubleHashing(list, mapSize);
        System.out.println("++++++++++++++++++++++++");

        long start  = System.currentTimeMillis();
        for (String s1 : list) {
            int i = getElementIndex(s1, mapSize);
            String s = indexArray[i];
            if (!s1.equals(s)) {
                throw new IllegalStateException();
            }
        }
        System.out.println("Find element using open addressing: " + (System.currentTimeMillis() - start) + " ms");

        Map<String, String> map = new HashMap<String, String>();
        for (String s1 : list) {
            map.put(s1, s1);
        }

        start  = System.currentTimeMillis();
        for (String s1 : list) {

            String s = map.get(s1);
            if (!s1.equals(s)) {
                throw new IllegalStateException();
            }
        }
        System.out.println("Find element using HashMap: " + (System.currentTimeMillis() - start) + " ms");

    }

    public static void testDoubleHashing(List<String> strings, int mapSize) {
        System.out.println("Double hashing");
        collision = 0;
        indexArray = new String[mapSize];

        for (String s : strings) {
            int i = doubleHash(s, mapSize);
            indexArray[i] = s;
        }

        System.out.println("Total collisions [" + collision + "]");
    }

    public static void testDivisionHashing(List<String> strings, int mapSize) {
        System.out.println("Standard");
        collision = 0;
        indexArray = new String[mapSize];

        for (String s : strings) {
            int i = divisionHash(s, mapSize);
            if (indexArray[i] != null) {
                collision++;
            }
            indexArray[i] = s;
        }
        System.out.println("Total collisions [" + collision + "]");
    }

    private static int divisionHash(String s, int bucketsSize) {
        int h1 = Math.abs(s.hashCode());
        return h1 % bucketsSize;
    }

    private static int doubleHash(String s, int m) {
        int sourceHash = Math.abs(s.hashCode());

        int h1 = sourceHash % m;
        int h2 = 1 + sourceHash % (m - 1);

        int probe = 0;
        int result = Math.abs(h1);
        String s1 = indexArray[result];

        while (s1 != null) {
            probe++;
            collision++;
            int x = (h1 + probe * h2) % m;
            s1 = indexArray[x];
            result = x;
        }
        return result;
    }

    private static int getElementIndex(String s, int m) {
        int sourceHash = Math.abs(s.hashCode());

        int h1 = sourceHash % m;
        int h2 = 1 + sourceHash % (m - 1);

        int probe = 0;
        int result = Math.abs(h1);
        String s1 = indexArray[result];
        while (!s1.equals(s)) {
            probe++;
            collision++;
            int x = (h1 + probe * h2) % m;
            s1 = indexArray[x];
            result = x;
        }
        return result;
    }
}
