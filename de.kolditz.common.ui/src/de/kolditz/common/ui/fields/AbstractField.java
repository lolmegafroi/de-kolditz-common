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
package de.kolditz.common.ui.fields;

import java.lang.reflect.Array;

import org.eclipse.swt.widgets.Composite;

import de.kolditz.common.concurrent.MultiThreaded;
import de.kolditz.common.util.IObservable;
import de.kolditz.common.util.IObservableBackend;
import de.kolditz.common.util.IObserver;

/**
 * Abstract base class for preference fields used in custom preference environments.
 * <p>Clients should access the {@link #labelText} field which is set in the constructor method.</p>
 * <p>The base implementation calls the following method(s):
 * <ul>
 * <li>{@link #createBackend()}</li>
 * </ul>
 * Clients must call the following method(s) themselves:
 * <ul>
 * <li>{@link #create()}</li>
 * <li>{@link #addListeners()}</li>
 * <li>{@link #setLabels()}</li>
 * <li>parent.register(this)</li>
 * </ul>
 * </p>
 * 
 * @author Till Kolditz - Till.Kolditz@gmail.com
 */
public abstract class AbstractField<E> implements IObservable<E>
{
    protected FieldComposite parent;
    protected IObservableBackend<E> observableBackEnd;
    protected boolean doUpdateBackEnd;

    protected int style;
    protected String labelText;

    /**
     * 
     * Clients must call the following method(s) themselves:
     * <ul>
     * <li>{@link #create()}</li>
     * <li>{@link #addListeners()}</li>
     * <li>{@link #setLabels()}</li>
     * <li>parent.register(this)</li>
     * </ul>
     * 
     * @param parent
     *            parent Composite
     * @param style
     *            Composite style
     * @param labelText
     *            the label's text
     */
    public AbstractField(FieldComposite parent, int style, String labelText)
    {
        assert parent != null : new IllegalArgumentException("parent = null"); //$NON-NLS-1$
        assert labelText != null : new IllegalArgumentException("label = null"); //$NON-NLS-1$

        this.parent = parent;
        this.style = style;
        this.labelText = labelText;
        observableBackEnd = createBackend();
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
     * "Protocol" method called after {@link #createBackend()}. This shall ensure same method names for better code
     * readability. "this" is the Composite for adding widgets.
     */
    protected abstract void create();

    /**
     * "Protocol" method called after {@link #addListeners()}. This shall ensure same method names for better code
     * readability.
     */
    protected abstract void setLabels();

    /**
     * "Protocol" method called after {@link #create()}. This shall ensure same method names for better code
     * readability.
     */
    protected abstract void addListeners();

    /**
     * This method is called by the {@link FieldComposite} to find out how many columns this field required.
     * 
     * @return how many columns this field required
     */
    protected abstract int getColumnsRequired();

    /**
     * This method is called by the {@link FieldComposite} to notify how many columns there will be in the layout.
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
     * @return this AbstractField's value
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
     * Sets this {@link AbstractField}'s value. Always notifies observers about the change.
     * 
     * @see #setValue(Object, boolean)
     * @param value
     *            this AbstractField's new value
     * @return this AbstractField's old value
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
     *            this AbstractField's new value
     * @param doNotifyObservers
     *            whether to notify observers or not
     * @return this AbstractField's old value
     */
    @MultiThreaded
    public abstract E setValue(E value, boolean doNotifyObservers);

    /**
     * The basic implementation sets only the very first of the given values. Client code overriding this method should
     * make it possible to set <b>null</b> values!
     * 
     * @param values
     *            this AbstractField's new values
     * @param doNotifyObservers
     *            whether to notify observers or not
     * @return this AbstractField's old values
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
