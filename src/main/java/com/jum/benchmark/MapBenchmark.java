package com.jum.benchmark;

import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;

import java.io.*;
import java.util.*;

/**
 * Benchmark of HashMap and TreeMap with jmh tool.
 *
 * See details on http://openjdk.java.net/projects/code-tools/jmh/
 */
@State(Scope.Benchmark)
public class MapBenchmark {
    private List<String> stringList;
    private Map<String, String> map;

    public MapBenchmark() {
        try {
            InputStream words = Thread.currentThread().getContextClassLoader().getResourceAsStream("words.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(words));
            String line;
            stringList =  new ArrayList<String>();
            while ((line = bufferedReader.readLine()) != null) {
                stringList.add(line);
            }
            hashMapInsert();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GenerateMicroBenchmark
    @BenchmarkMode(value = Mode.AverageTime)
    public void hashMapInsert() {
        map = new HashMap<String, String>();
        for (String s : stringList) {
            map.put(s, s);
        }
    }

    @GenerateMicroBenchmark
    @BenchmarkMode(value = Mode.AverageTime)
    public void hashMapFind() {
        for (String s : stringList) {
            String s1 = map.get(s);
            assert (s1 != null);
        }
    }

    @GenerateMicroBenchmark
    @BenchmarkMode(value = Mode.AverageTime)
    public void treeMapInsert() {
        map = new TreeMap<String, String>();
        for (String s : stringList) {
            map.put(s, s);
        }
    }

    @GenerateMicroBenchmark
    @BenchmarkMode(value = Mode.AverageTime)
    public void treeMapFind() {
        for (String s : stringList) {
            String s1 = map.get(s);
            assert (s1 != null);
        }
    }

    public static void main(String[] args) throws IOException {
        Main.main(getArguments(".*Map.*", 10, 3, 5));
    }

    private static String[] getArguments(String className, int nRuns, int warmups, int forks) {
        return new String[]{className,
                "-i", "" + nRuns,
                "-wi","" + warmups,
                "-f", "" + forks

        };
    }
}
