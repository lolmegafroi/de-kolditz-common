/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 * created on 13.09.2012 at 19:44:04
 * 
 *  Contributors:
 *      Till Kolditz
 *******************************************************************************/
package de.kolditz.common.s11n;

import java.util.LinkedList;
import java.util.List;

/**
 * A light-weight, non thread-safe serializer for whole arrays / collections. Does currently not support nested arrays.
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public abstract class ArrayDeserializer extends DeSerializer
{
    /**
     * Tries to deserialize a whole array of objects which was serialized by an {@link ArraySerializer}. This should be
     * an escaped string.
     * 
     * @param serialized
     *            the serialized string representation
     * @return the found objects' fields
     */
    public static String[][] deserialize(String serialized)
    {
        if (serialized.length() < 3)
            return new String[0][0];
        // init
        List<String[]> list = new LinkedList<String[]>();
        int charCount = serialized.codePointCount(0, serialized.length());
        int maxPos = charCount;
        int[] chars = new int[charCount];
        // convert everything to true unicode
        for (int i = 0; i < charCount; ++i)
        {
            chars[i] = serialized.codePointAt(i);
        }
        // 1) search for array delimiters
        int pos = search(chars, 0, maxPos, ARR_BEG);
        int end = search(chars, pos, maxPos, ARR_END);
        if (chars[end] != ARRAY_END)
            return new String[0][0];
        // 2) search for objects and then for the fields
        int next;
        while (pos < end)
        {
            pos = search(chars, pos, end, OBJ_BEG);
            next = search(chars, pos, end, OBJ_END);
            if (chars[pos] != OBJECT_BEGIN || chars[next] != OBJECT_END)
                break; // no further object found
            // include the object delimiters
            list.add(ObjectDeserializer.deserializeAsArray(new String(chars, pos, next - pos + 1)));
            pos = next + 1;
        }
        String[][] result = new String[list.size()][];
        for (int i = 0; i < list.size(); ++i)
        {
            result[i] = list.get(i);
        }
        return result;
    }
}
