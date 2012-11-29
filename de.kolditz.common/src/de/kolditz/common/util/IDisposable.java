/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 * created on 26.11.2012 at 15:26:22
 * 
 *  Contributors:
 *      Till Kolditz
 *******************************************************************************/
package de.kolditz.common.util;

/**
 * Interface for classes whose instances shall be disposable in any sense. This may be e.g. to release soem system
 * resources.
 * 
 * @author Till Kolditz - Till.Kolditz@gmail.com
 */
public interface IDisposable
{
    /**
     * Disposes of this object. This is an explicit protocol function e.g. for gracefully releasing system resources.
     */
    void dispose();
}
