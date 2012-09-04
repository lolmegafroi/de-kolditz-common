/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     Till Kolditz
 *******************************************************************************/
package de.kolditz.common.util;

/**
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public interface IObservable<E> {
    /**
     * Registers an observer to this object's list of observers.
     * 
     * @param observer
     *            the {@link IObserver}
     * @return whether the observer could be added
     */
    boolean registerObserver(IObserver<E> observer);

    /**
     * Unregisters an observer fromthis object's list of observers.
     * 
     * @param observer
     *            the {@link IObserver}
     * @return whether the observer could be added
     */
    boolean unregisterObserver(IObserver<E> observer);
}
