package com.geohash.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: mdyminski
 */
public class Buffer<T> implements Serializable {

    private volatile List<T> buffer;

    public Buffer() {
        buffer = new ArrayList<T>();
    }

    public synchronized void add(T toadd) {
        buffer.add(toadd);
    }

    public int size() {
        return buffer.size();
    }

    public synchronized List<T> empty() {
        List<T> original = buffer;
        buffer = new ArrayList<>();
        return original;
    }

}
