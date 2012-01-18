/**
 * created on 13.12.2011 at 15:12:06
 */
package de.kolditz.common;

import java.util.Map.Entry;

/**
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public class Pair<K, V> implements Entry<K, V> {
    protected K first;
    protected V second;

    public Pair(K first, V second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public K getKey() {
        return first;
    }

    public K first() {
        return first;
    }

    /**
     * Set the {@link #first} value and return this pair.
     * 
     * @param first
     * @return
     */
    public Pair<K, V> first(K first) {
        this.first = first;
        return this;
    }

    @Override
    public V getValue() {
        return second;
    }

    public V second() {
        return second;
    }

    public Pair<K, V> second(V second) {
        this.second = second;
        return this;
    }

    @Override
    public V setValue(V value) {
        V old = this.second;
        this.second = value;
        return old;
    }
}
