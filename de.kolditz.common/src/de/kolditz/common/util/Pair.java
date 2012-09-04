/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     Till Kolditz
 *******************************************************************************/
package de.kolditz.common.util;

import java.util.Map.Entry;

/**
 * Implementation for {@link Entry}
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public class Pair<K, V> implements Entry<K, V>, Cloneable
{
    protected K first;
    protected V second;

    /**
     * Constructor.
     * 
     * @param first
     *            first value or key
     * @param second
     *            (second) value
     */
    public Pair(K first, V second)
    {
        this.first = first;
        this.second = second;
    }

    @Override
    public K getKey()
    {
        return first;
    }

    /**
     * @see #getKey()
     */
    public K first()
    {
        return first;
    }

    /**
     * Set the {@link #first} value and return this pair. Can be used for command chaining.
     * 
     * @param first
     *            the first value/key
     * @return this object
     */
    public Pair<K, V> first(K first)
    {
        this.first = first;
        return this;
    }

    @Override
    public V getValue()
    {
        return second;
    }

    /**
     * @see #getValue()
     */
    public V second()
    {
        return second;
    }

    /**
     * Set the {@link #second} value and return this pair. Can be used for command chaining.
     * 
     * @param second
     *            the (second) value
     * @return this object
     */
    public Pair<K, V> second(V second)
    {
        this.second = second;
        return this;
    }

    @Override
    public V setValue(V value)
    {
        V old = this.second;
        this.second = value;
        return old;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Pair<K, V> clone() throws CloneNotSupportedException
    {
        return (Pair<K, V>)super.clone();
    }
}
