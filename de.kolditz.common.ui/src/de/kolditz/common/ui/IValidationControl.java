/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 * created on 23.08.2012 at 15:43:59
 * 
 *  Contributors:
 *      Till Kolditz
 *******************************************************************************/
package de.kolditz.common.ui;

/**
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public interface IValidationControl {
    /**
     * @return whether this control's content is valid
     */
    boolean isValid();
}
