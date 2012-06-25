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
package de.kolditz.common.ui.preferences;

import org.eclipse.swt.widgets.Composite;

import de.kolditz.common.IObservable;
import de.kolditz.common.IObservableBackend;
import de.kolditz.common.IObserver;
import de.kolditz.common.concurrent.MultiThreaded;

/**
 * Abstract base class for preference fields used in custom preference environments.
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public abstract class PreferenceField<E> extends Composite implements IObservable<E> {
    protected IObservableBackend<E> observableBackEnd;
    protected boolean doUpdateBackEnd;

    /**
     * @param parent
     *            parent Composite
     * @param style
     *            Composite style
     */
    public PreferenceField(Composite parent, int style) {
        super(parent, style);
        observableBackEnd = createBackend();
    }

    /**
     * Registers an {@link IObserver} for modification events. The data object is the new text.
     */
    @Override
    public boolean registerObserver(IObserver<E> observer) {
        return observableBackEnd.registerObserver(observer);
    }

    /**
     * Unregisters an {@link IObserver} from modification events.
     */
    @Override
    public boolean unregisterObserver(IObserver<E> observer) {
        return observableBackEnd.unregisterObserver(observer);
    }

    protected void notifyObservers(E value) {
        observableBackEnd.update(value);
    }

    /**
     * Needed to create the generic backend. Client code should look like
     * 
     * <pre>
     * return new IObservableBackend&lt;TYPE&gt;(this);
     * </pre>
     * 
     * @return the IObservableBackend
     */
    protected abstract IObservableBackend<E> createBackend();

    /**
     * "Protocol" method that must be called by the client. This shall ensure same method names for better code
     * readability. "this" is the Composite for adding widgets.
     */
    protected abstract void create();

    /**
     * "Protocol" method that must be called by the client. This shall ensure same method names for better code
     * readability.
     */
    protected abstract void setLabels();

    /**
     * "Protocol" method that must be called by the client. This shall ensure same method names for better code
     * readability.
     */
    protected abstract void addListeners();

    /**
     * @return this PreferenceField's value
     */
    @MultiThreaded
    public abstract E getValue();

    /**
     * Sets this {@link PreferenceField}'s value. Allways notifies observers about the change.
     * 
     * @see #setValue(Object, boolean)
     * @param value
     *            this PreferenceField's new value
     * @return this PreferenceField's old value
     */
    @MultiThreaded
    public E setValue(E value) {
        return setValue(value, true);
    }

    /**
     * @param value
     *            this PreferenceField's new value
     * @param doNotifyObservers
     *            whether to notify observers or not
     * @return this PreferenceField's old value
     */
    @MultiThreaded
    public abstract E setValue(E value, boolean doNotifyObservers);
}
