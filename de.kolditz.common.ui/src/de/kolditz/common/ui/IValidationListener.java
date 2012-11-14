/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 * created on 14.11.2012 at 19:44:34
 * 
 *  Contributors:
 *      Till Kolditz
 *******************************************************************************/
package de.kolditz.common.ui;

/**
 * Interface for listeners on validation events.
 * 
 * @author Till Kolditz - Till.Kolditz@gmail.com
 */
public interface IValidationListener
{
    void valid(boolean valid);
}
