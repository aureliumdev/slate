package dev.aurelium.slate.util;

public class Pair<T, V> {

    private final T first;
    private final V second;

    public Pair(T first, V second) {
        this.first = first;
        this.second = second;
    }

    public T first() {
        return first;
    }

    public V second() {
        return second;
    }
}
