/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 * created on 13.09.2012 at 17:29:43
 * 
 *  Contributors:
 *      Till Kolditz
 *******************************************************************************/
package de.kolditz.common.s11n;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A non thread-safe serializer for whole arrays / collections. Does currently not support nested arrays.
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public class ArraySerializer extends DeSerializer
{
    /**
     * Serializes a whole array of objects using the ArraySerializer's methods.
     * 
     * @param objects
     *            the array of objects
     * @return the serialization string
     */
    public static String serialize(Object... objects)
    {
        List<Object> list = new ArrayList<Object>(objects.length);
        for (Object o : objects)
            list.add(o);
        return serialize(list);
    }

    /**
     * Serializes a whole collection of objects using the ArraySerializer's methods.
     * 
     * @param objects
     *            the collection of objects
     * @return the serialization string
     */
    public static String serialize(Collection<?> objects)
    {
        ArraySerializer serializer = new ArraySerializer();
        for (Object o : objects)
        {
            serializer.addObject(o);
        }
        return serializer.serialize();
    }

    private StringBuilder sb = new StringBuilder();
    private boolean finished = false;

    private void check(Object argument)
    {
        assert argument != null;

        if (sb.length() == 0)
        {
            sb.append(ARRAY_BEGIN);
        }
        else
        {
            if (finished)
            {
                sb.deleteCharAt(sb.length() - 1);
                finished = false;
            }
            if (sb.codePointAt(sb.length() - 1) == OBJECT_END)
            {
                sb.append(SEPARATOR);
            }
        }
    }

    /**
     * Adds another Object to this serializer denoted by this object.
     * 
     * @param fields
     *            the object's fields' values
     * @see ObjectSerializer#serialize(StringBuilder, Object)
     */
    public void addObject(Object object)
    {
        check(object);
        ObjectSerializer.serialize(sb, object);
    }

    /**
     * Adds another Object to this serializer denoted by this array of strings (its fields' string values)
     * 
     * @param fields
     *            the object's fields' values
     */
    public void addObject(String... fields)
    {
        check(fields);
        ObjectSerializer.serialize(sb, fields);
    }

    /**
     * Adds another Object to this serializer denoted by this array of strings (its fields' string values)
     * 
     * @param fields
     *            the object's fields' values
     */
    public void addObject(Collection<String> fields)
    {
        check(fields);
        ObjectSerializer.serialize(sb, fields);
    }

    /**
     * Returns the whole serialized array of objects. Further objects may be added after this method was called.
     * 
     * @return the whole serialization string.
     */
    public String serialize()
    {
        if (!finished)
        {
            finished = true;
            sb.append(ARRAY_END);
        }
        return sb.toString();
    }
}
