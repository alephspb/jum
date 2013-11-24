package com.jum.util;

import java.io.Serializable;

public class TestKey implements Serializable {
    private int hash;
    private int eq;

    public TestKey(int hash, int eq) {
        this.hash = hash;
        this.eq = eq;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        return ((TestKey) obj).eq == this.eq;
    }
}
