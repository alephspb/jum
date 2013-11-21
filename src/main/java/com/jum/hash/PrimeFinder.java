package com.jum.hash;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrimeFinder {
    private List<Integer> primes = new ArrayList<Integer>(Arrays.asList(2));
    private int current = 2;

    public Integer next() {
        while (true) {
            current++;
            boolean f = true;
            for (Integer prime : primes) {
                int mod = current % prime;
                if (mod == 0) {
                    f = false;
                    break;
                }
            }
            if (f) {
                primes.add(current);
                return current;
            }
        }
    }

    public Integer greaterThen(int x) {
        int val;
        while((val = next()) <= x) {
            //nothing to do
        }
        return val;
    }

    public static void main(String[] args) {
        PrimeFinder primeFinder = new PrimeFinder();
        System.out.println(primeFinder.greaterThen(17));
    }
}

