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

import java.lang.reflect.Array;

import org.eclipse.swt.widgets.Composite;

import de.kolditz.common.concurrent.MultiThreaded;
import de.kolditz.common.util.IObservable;
import de.kolditz.common.util.IObservableBackend;
import de.kolditz.common.util.IObserver;

/**
 * Abstract base class for preference fields used in custom preference environments.
 * 
 * @author Till Kolditz - Till.Kolditz@gmail.com
 */
public abstract class PreferenceField<E> implements IObservable<E>
{
    protected PreferencesComposite parent;
    protected IObservableBackend<E> observableBackEnd;
    protected boolean doUpdateBackEnd;

    /**
     * @param parent
     *            parent Composite
     * @param style
     *            Composite style
     */
    public PreferenceField(PreferencesComposite parent, int style)
    {
        this.parent = parent;
        observableBackEnd = createBackend();
        parent.registerField(this);
    }

    /**
     * Registers an {@link IObserver} for modification events. The data object is the new text.
     */
    @Override
    public boolean registerObserver(IObserver<E> observer)
    {
        return observableBackEnd.registerObserver(observer);
    }

    /**
     * Unregisters an {@link IObserver} from modification events.
     */
    @Override
    public boolean unregisterObserver(IObserver<E> observer)
    {
        return observableBackEnd.unregisterObserver(observer);
    }

    protected void notifyObservers(E value)
    {
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
    protected IObservableBackend<E> createBackend()
    {
        return new IObservableBackend<E>(this);
    }

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
     * This method is called by the {@link PreferencesComposite} to find out how many columns this field required.
     * 
     * @return how many columns this field required
     */
    protected abstract int getColumnsRequired();

    /**
     * This method is called by the {@link PreferencesComposite} to notify how many columns there will be in the layout.
     * It is guaranteed to be called after {@link #create()}.
     * 
     * @param columns
     *            the number of columns the parent layout will have
     */
    protected abstract void setColumns(int columns);

    protected Composite getComposite()
    {
        return parent.getComposite();
    }

    public abstract void setEnabled(boolean enabled);

    /**
     * @return this PreferenceField's value
     */
    @MultiThreaded
    public abstract E getValue();

    /**
     * @return an Array of values, for when multiple values are possible
     */
    @SuppressWarnings("unchecked")
    @MultiThreaded
    public E[] getValues()
    {
        E value = getValue();
        E[] result = (E[])Array.newInstance(value.getClass(), 1);
        result[0] = value;
        return result;
    }

    /**
     * Sets this {@link PreferenceField}'s value. Always notifies observers about the change.
     * 
     * @see #setValue(Object, boolean)
     * @param value
     *            this PreferenceField's new value
     * @return this PreferenceField's old value
     */
    @MultiThreaded
    public E setValue(E value)
    {
        return setValue(value, true);
    }

    /**
     * Client code should make it possible to set <b>null</b> values!
     * 
     * @param value
     *            this PreferenceField's new value
     * @param doNotifyObservers
     *            whether to notify observers or not
     * @return this PreferenceField's old value
     */
    @MultiThreaded
    public abstract E setValue(E value, boolean doNotifyObservers);

    /**
     * The basic implementation sets only the very first of the given values. Client code overriding this method should
     * make it possible to set <b>null</b> values!
     * 
     * @param values
     *            this PreferenceField's new values
     * @param doNotifyObservers
     *            whether to notify observers or not
     * @return this PreferenceField's old values
     */
    @MultiThreaded
    public E[] setValues(E[] values, boolean doNotifyObservers)
    {
        E[] oldValues = getValues();
        if(values == null || values.length == 0)
        {
            setValue(null);
        }
        else
        {
            setValue(values[0]);
        }
        return oldValues;
    }

    /**
     * Returns <code>true</code> if the widget has been disposed,
     * and <code>false</code> otherwise.
     * <p>
     * This method gets the dispose state for the widget.
     * When a widget has been disposed, it is an error to
     * invoke any other method (except {@link #dispose()}) using the widget.
     * </p>
     *
     * @return <code>true</code> when the widget is disposed and <code>false</code> otherwise
     */
    public abstract boolean isDisposed();
}
