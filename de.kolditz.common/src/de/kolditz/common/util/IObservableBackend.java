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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A back-end implementation for {@link IObservable}s. This is intended to reduce implementation effort for front-end
 * classes by just delegating calls to an ObservableBackend object.
 * 
 * @author Till Kolditz - Till.Kolditz@gmail.com
 */
public class IObservableBackend<E> implements IObservable<E>
{
    private IObservable<E> frontEnd;
    private Map<IObserver<E>, Integer> observables;

    public IObservableBackend(IObservable<E> frontEnd)
    {
        this.frontEnd = frontEnd;
        observables = new HashMap<IObserver<E>, Integer>();
    }

    @Override
    public boolean registerObserver(IObserver<E> observer)
    {
        if (observer == null)
            throw new IllegalArgumentException("observer is null"); //$NON-NLS-1$
        observables.put(observer, Integer.valueOf(0));
        return true;
    }

    @Override
    public boolean unregisterObserver(IObserver<E> observer)
    {
        if (observer == null)
            throw new IllegalArgumentException("observer is null"); //$NON-NLS-1$
        observables.remove(observer);
        return true;
    }

    /**
     * Updates all registered {@link IObserver}s.
     * 
     * @param data
     *            the application-specific data object
     */
    public void update(E data)
    {
        for (Entry<IObserver<E>, Integer> e : observables.entrySet())
        {
            e.getKey().update(frontEnd, data);
        }
    }

    /**
     * Updates all registered {@link IObserver}s.
     * 
     * @param data
     *            the application-specific data object
     */
    public void update(E[] data)
    {
        for (Entry<IObserver<E>, Integer> e : observables.entrySet())
        {
            e.getKey().update(frontEnd, data);
        }
    }

    /**
     * Updates all registered {@link IObserver}s.
     * 
     * @param data
     *            the application-specific data object
     */
    public void update(Collection<E> data)
    {
        for (Entry<IObserver<E>, Integer> e : observables.entrySet())
        {
            e.getKey().update(frontEnd, data);
        }
    }

    /**
     * Updates all registered {@link IObserver}s except the one given.
     * 
     * @param data
     *            the application-specific data object
     * @param notToNotify
     *            the observer which shall not be updated. may be null
     */
    public void update(E data, IObserver<E> notToNotify)
    {
        if (notToNotify == null)
        {
            update(data);
        }
        else
        {
            for (Entry<IObserver<E>, Integer> e : observables.entrySet())
            {
                if (e.getKey() != notToNotify)
                    e.getKey().update(frontEnd, data);
            }
        }
    }

    /**
     * Updates all registered {@link IObserver}s except the one given.
     * 
     * @param data
     *            the application-specific data object
     * @param notToNotify
     *            the observer which shall not be updated. may be null
     */
    public void update(E[] data, IObserver<E> notToNotify)
    {
        if (notToNotify == null)
        {
            update(data);
        }
        else
        {
            for (Entry<IObserver<E>, Integer> e : observables.entrySet())
            {
                if (e.getKey() != notToNotify)
                    e.getKey().update(frontEnd, data);
            }
        }
    }

    /**
     * Updates all registered {@link IObserver}s except the one given.
     * 
     * @param data
     *            the application-specific data object
     * @param notToNotify
     *            the observer which shall not be updated. may be null
     */
    public void update(Collection<E> data, IObserver<E> notToNotify)
    {
        if (notToNotify == null)
        {
            update(data);
        }
        else
        {
            for (Entry<IObserver<E>, Integer> e : observables.entrySet())
            {
                if (e.getKey() != notToNotify)
                    e.getKey().update(frontEnd, data);
            }
        }
    }
}
