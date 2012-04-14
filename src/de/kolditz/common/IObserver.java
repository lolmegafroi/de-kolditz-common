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
package de.kolditz.common;

/**
 * An interface for objects that want to observer other objects, being {@link #update(IObservable, Object)}d upon
 * changes in the {@link IObservable}
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public interface IObserver<E> {
    /**
     * Notifies observing {@link IObservable}s upon important state changes. The actual semantics are
     * application-specific.
     * 
     * @param source
     *            the {@link IObservable} that notifies the observer
     * @param data
     *            application-specific data. Clients should override this javadoc to give more detailed information.
     */
    void update(IObservable<E> source, E data);
}
