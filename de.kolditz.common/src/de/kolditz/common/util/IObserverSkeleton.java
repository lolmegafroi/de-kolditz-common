/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 * created on 17.10.2012 at 17:01:40
 * 
 *  Contributors:
 *      Till Kolditz
 *******************************************************************************/
package de.kolditz.common.util;

import java.util.Collection;

/**
 * A simple IObserver skeleton which does nothing.
 *
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public class IObserverSkeleton<E> implements IObserver<E>
{
    @Override
    public void update(IObservable<E> source, E data)
    {
    }

    @Override
    public void update(IObservable<E> source, E[] data)
    {
    }

    @Override
    public void update(IObservable<E> source, Collection<E> data)
    {
    }
}
