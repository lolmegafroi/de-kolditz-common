/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 * created on 13.09.2012 at 17:40:42
 * 
 *  Contributors:
 *      Till Kolditz
 *******************************************************************************/
package de.kolditz.common.s11n;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A simple non thread-safe object serializer. For de-/serializing arrays or collections use {@link ArraySerializer} and
 * {@link ArrayDeserializer}.
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public class ObjectSerializer extends DeSerializer
{
    /**
     * Clients must know the order of serialized fields for deserialization. Expects unescaped strings, since this
     * method does escape some characters.
     * 
     * @param fields
     * @return
     * @see ObjectDeserializer#deserializeAsArray(String)
     */
    public static String serialize(String... fields)
    {
        assert fields != null;
        StringBuilder sb = new StringBuilder();
        serialize(sb, fields);
        return sb.toString();
    }

    /**
     * Clients must know the order of serialized fields for deserialization. Expects unescaped strings, since this
     * method does escape some characters.
     * 
     * @param fields
     * @return
     * @see ObjectDeserializer#deserializeAsArray(String)
     */
    public static String serialize(Collection<String> fields)
    {
        assert fields != null;
        StringBuilder sb = new StringBuilder();
        serialize(sb, fields);
        return sb.toString();
    }

    public static void serialize(StringBuilder sb, Object object)
    {
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getFields();
        List<String> list = new ArrayList<String>(fields.length);
        if (fields.length > 0)
        {
            for (Field f : clazz.getFields())
            {
                int mod = f.getModifiers();
                // non-static and non-transient fields
                if ((mod & Modifier.STATIC) == 0 && (mod & Modifier.TRANSIENT) == 0)
                {
                    try
                    {
                        list.add(String.valueOf(f.get(object)));
                    }
                    catch (IllegalArgumentException e)
                    {
                        // print, ignore and proceed
                        e.printStackTrace();
                    }
                    catch (IllegalAccessException e)
                    {
                        // print, ignore and proceed
                        e.printStackTrace();
                    }
                }
            }
        }
        serialize(sb, list);
    }

    /**
     * Simply appends to the given StringBuilder. Clients must know the order of serialized fields for deserialization.
     * Expects unescaped strings, since this method does escape some characters.
     * 
     * @param fields
     * @return
     * @see ObjectDeserializer#deserializeAsArray(String)
     */
    public static void serialize(StringBuilder sb, String... fields)
    {
        assert sb != null;
        assert fields != null;
        String f;
        int length = fields.length - 1;
        sb.append(OBJECT_BEGIN);
        for (int i = 0; i <= length; ++i)
        {
            f = fields[i];
            sb.append(f != null ? escape(f) : NULL);
            if (i < length)
                sb.append(SEPARATOR);
        }
        sb.append(OBJECT_END);
    }

    /**
     * Simply appends to the given StringBuilder. Clients must know the order of serialized fields for deserialization.
     * Expects unescaped strings, since this method does escape some characters.
     * 
     * @param fields
     * @return
     * @see ObjectDeserializer#deserializeAsArray(String)
     */
    public static void serialize(StringBuilder sb, Collection<String> fields)
    {
        assert sb != null;
        assert fields != null;
        int length = fields.size() - 1;
        sb.append(OBJECT_BEGIN);
        int i = 0;
        for (String f : fields)
        {
            sb.append(f != null ? escape(f) : NULL);
            if (i < length)
                sb.append(SEPARATOR);
            ++i;
        }
        sb.append(OBJECT_END);
    }
}
