/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 * created on 29.08.2012 at 20:21:21
 * 
 *  Contributors:
 *      Till Kolditz
 *******************************************************************************/
package de.kolditz.common.util;

/**
 * 
 * @author Till Kolditz - Till.Kolditz@gmail.com
 */
public interface IValidator<E>
{
    /**
     * Validates this object.
     * 
     * @param object
     *            the object to validate, may be null
     * @return null if this object is valid, any other string otherwise
     */
    public String validate(E object);
}
