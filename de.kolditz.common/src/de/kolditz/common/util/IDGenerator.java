/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 * created on 22.11.2012 at 17:15:48
 * 
 *  Contributors:
 *      Till Kolditz
 *******************************************************************************/
package de.kolditz.common.util;

/**
 * @author Till Kolditz - Till.Kolditz@gmail.com
 */
public class IDGenerator
{
    private int start;

    /**
     * Creates an ID generator starting from 0.
     */
    public IDGenerator()
    {
        this(0);
    }

    /**
     * Creates an ID generator starting from the given parameter.
     * 
     * @param start
     *            from where to start generating IDs.
     */
    public IDGenerator(int start)
    {
        this.start = start;
    }

    public synchronized int getNext()
    {
        return start++;
    }
}
