/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 * created on 21.08.2012 at 17:46:26
 * 
 *  Contributors:
 *      Till Kolditz
 *******************************************************************************/
package de.kolditz.common.ui;

/**
 * Interface for classes which are aware of the fillView concept
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public interface IFillViewAware {
    void setFillView(boolean isFillView);

    boolean isFillView();
}
