/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 * created on 12.07.2012 at 17:45:59
 * 
 *  Contributors:
 *      Till Kolditz
 *******************************************************************************/
package de.kolditz.common.remote;

/**
 * @author Till Kolditz - Till.Kolditz@gmail.com
 */
public class NotConnectedException extends IllegalStateException
{
    /**
     * 
     */
    private static final long serialVersionUID = -7820968310772865935L;

    public NotConnectedException()
    {
        super();
    }

    public NotConnectedException(String s)
    {
        super(s);
    }

    public NotConnectedException(Throwable cause)
    {
        super(cause);
    }

    public NotConnectedException(String s, Throwable cause)
    {
        super(s, cause);
    }
}
