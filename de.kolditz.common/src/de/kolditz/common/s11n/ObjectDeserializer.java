/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 * created on 13.09.2012 at 19:35:37
 * 
 *  Contributors:
 *      Till Kolditz
 *******************************************************************************/
package de.kolditz.common.s11n;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * A light-weight, non thread-safe object deserializer. For de-/serializing arrays or collections use
 * {@link ArraySerializer} and {@link ArrayDeserializer}.
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public abstract class ObjectDeserializer extends DeSerializer
{
    /**
     * Tries to deserialize a single object which was serialized by the {@link ObjectSerializer#serialize(String...)} or
     * {@link ObjectSerializer#serialize(Collection)} method. This should be an escaped string.
     * 
     * @param serialized
     *            the single string
     * @return the found fields as an array, or null if the string is too short or could not be correctly parsed
     */
    public static String[] deserializeAsArray(String serialized)
    {
        if (serialized.length() < 3)
            return null;
        // init
        List<String> list = new LinkedList<String>();
        int charCount = serialized.codePointCount(0, serialized.length());
        int maxPos = charCount - 1;
        int[] unicodes = new int[charCount];
        // convert everything to true unicode
        for (int i = 0; i < charCount; ++i)
        {
            unicodes[i] = serialized.codePointAt(i);
        }
        // 1) search for object delimiters
        int pos = search(unicodes, 0, maxPos, OBJ_BEG);
        ++pos;
        int end = search(unicodes, pos, maxPos, OBJ_END);
        if (unicodes[end] != OBJECT_END)
            return null;
        // 2) search for fields
        int next;
        while (pos < end)
        {
            next = search(unicodes, pos, end, SEP_OR_OBJEND);
            list.add(unescape(new String(unicodes, pos, next - pos)));
            pos = next + 1; // after the previous separator
        }
        return list.toArray(new String[list.size()]);
    }

    /**
     * Tries to deserialize a single object which was serialized by the {@link ObjectSerializer#serialize(String...)} or
     * {@link ObjectSerializer#serialize(Collection)} method. This should be an escaped string.
     * 
     * @param serialized
     *            the single string
     * @return the found fields as a collection
     */
    public static Collection<String> deserializeAsList(String serialized)
    {
        if (serialized.length() < 3)
            return null;
        // init
        List<String> list = new LinkedList<String>();
        int charCount = serialized.codePointCount(0, serialized.length());
        int maxPos = charCount - 1;
        int[] unicodes = new int[charCount];
        // convert everything to true unicode
        for (int i = 0; i < charCount; ++i)
        {
            unicodes[i] = serialized.codePointAt(i);
        }
        // 1) search for object delimiters
        int pos = search(unicodes, 0, maxPos, OBJ_BEG);
        ++pos;
        int end = search(unicodes, pos, maxPos, OBJ_END);
        if (unicodes[end] != OBJECT_END)
            return null;
        // 2) search for fields
        int next;
        while (pos < end)
        {
            next = search(unicodes, pos, end, SEP_OR_OBJEND);
            list.add(unescape(new String(unicodes, pos, next - pos)));
            pos = next + 1; // after the previous separator
        }
        return list;
    }
}
