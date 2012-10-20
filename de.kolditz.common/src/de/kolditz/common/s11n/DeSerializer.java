/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 * created on 13.09.2012 at 17:30:09
 * 
 *  Contributors:
 *      Till Kolditz
 *******************************************************************************/
package de.kolditz.common.s11n;

/**
 * Abstract base utility class for de-/serialization.
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public abstract class DeSerializer
{
    public static final char ARRAY_BEGIN = '[';

    public static final int[] ARR_BEG = new int[] { ARRAY_BEGIN };

    public static final char ARRAY_END = ']';

    public static final int[] ARR_END = new int[] { ARRAY_END };

    public static final char OBJECT_BEGIN = '{';

    public static final int[] OBJ_BEG = new int[] { OBJECT_BEGIN };

    public static final char OBJECT_END = '}';

    public static final int[] OBJ_END = new int[] { OBJECT_END };

    public static final char SEPARATOR = ',';

    public static final int[] SEP = new int[] { SEPARATOR };

    public static final int[] SEP_OR_OBJEND = new int[] { SEPARATOR, OBJECT_END };

    public static final int BACKSLASH = '\\';

    public static final String DOUBLE_BACKSLASH = "\\\\"; //$NON-NLS-1$

    public static final String NULL = "\\0"; //$NON-NLS-1$

    /**
     * Searches for the given characters "toFind" in the character array "chars" starting at "pos" until at most
     * "maxPos" and returns the index of the first occurence of any of the "toFind" characters. Uses code points (32 bit
     * unicode). Clients must take care of escaped and unescaped strings themselves since this function does no escaping
     * /unescaping!
     * 
     * @param chars
     *            the character array in which to search.
     * @param pos
     *            the starting position.
     * @param maxPos
     *            the last position to search at.
     * @param toFind
     *            the character to find.
     * @return the position where the character was found or maxPos.
     * @see #escape(String)
     * @see #unescape(String)
     */
    protected static int search(int[] chars, int pos, int maxPos, int[] toFind)
    {
        int c = chars[pos];
        do
        {
            for (int find : toFind)
            {
                if (c == find)
                {
                    return pos;
                }
            }
            if (pos >= maxPos)
            {
                break;
            }
            c = chars[++pos];
        }
        while (true);
        return pos;
    }

    /**
     * Tries to retrieve the next object as a token (String) delimited the first one of delimiters which is found.
     * Searches in "chars" from "posArray[0]" until at most "maxPos". Uses code points (32 bit unicode). Clients must
     * take care of escaped and unescaped strings themselves since this function does no escaping/unescaping!
     * 
     * @param chars
     *            the character array in which to search.
     * @param posArray
     *            the array containing the start position. After this call, the position will have this call's.
     * @param maxPos
     *            the last position to search at.
     * @param delimiters
     *            the delimiter characters.
     * @return the String token or null if none of the "delimiters" were not found.
     * @see #escape(String)
     * @see #unescape(String)
     */
    protected static String getNext(int[] chars, int[] posArray, int maxPos, int[] delimiters)
    {
        int offset, count;
        int pos = posArray[0];
        offset = ++pos;
        pos = search(chars, pos, maxPos, delimiters);
        // eof?
        if (pos >= maxPos)
        {
            return null;
        }
        // first attribute found, pos is at separator
        count = pos - offset;
        posArray[0] = pos;
        return new String(chars, offset, count);
    }

    /**
     * Escapes the occurences of special characters like '{', '[', ']', or '}'.
     * 
     * @param unescaped
     *            an unescaped string
     * @return the escaped string
     */
    protected static String escape(String unescaped)
    {
        if (unescaped == null)
            return NULL;
        StringBuilder sb = null;
        int codePoint;
        int length = unescaped.length();
        for (int i = 0; i < length; ++i)
        {
            codePoint = unescaped.codePointAt(i);
            if (codePoint == ARRAY_BEGIN || codePoint == ARRAY_END || codePoint == OBJECT_BEGIN
                    || codePoint == OBJECT_END || codePoint == SEPARATOR || codePoint == BACKSLASH)
            {
                // lazy initialization on first occurence
                if (sb == null)
                    sb = new StringBuilder(i > 0 ? unescaped.subSequence(0, i) : "");
                sb.append(BACKSLASH).append(codePoint);
            }
            else
            {
                if (sb != null)
                {
                    char[] chars = Character.toChars(codePoint);
                    sb.append(chars);
                    if (chars.length > 1)
                        ++i; // skip one index if it's a 32-bit unicode char
                }
            }
        }
        return sb == null ? unescaped : sb.toString();
    }

    /**
     * Unescapes the occurences of escaped sequences like "\{", "\[", "\]", or "\}".
     * 
     * @param escaped
     *            a string which potentially contains escaped sequences
     * @return a string without escaped sequences, possibly the string itself, or null if "escaped" is an escaped null
     *         string
     */
    protected static String unescape(String escaped)
    {
        int pos = escaped.indexOf(BACKSLASH);
        if (pos == -1)
            return escaped;
        if (pos == escaped.length() - 1)
            return escaped.substring(0, escaped.length() - 1);
        if (escaped.equals(NULL))
            return null;
        StringBuilder sb = new StringBuilder();
        int character;
        int oldPos = 0;
        do
        {
            if (oldPos < pos)
                sb.append(escaped.substring(oldPos, pos));
            character = escaped.codePointAt(++pos); // get the code point after the backslash
            switch (character)
            {
                case ARRAY_BEGIN:
                case ARRAY_END:
                case OBJECT_BEGIN:
                case OBJECT_END:
                case BACKSLASH:
                case SEPARATOR:
                    sb.append(character);
            }
            pos += Character.charCount(character);
            oldPos = pos;
            pos = escaped.indexOf(BACKSLASH, pos);
        }
        while (pos != -1);
        return sb.toString();
    }

    public static boolean hasLargeUnicodeChars(String s)
    {
        return s.length() != s.codePointCount(0, s.length());
    }
}
