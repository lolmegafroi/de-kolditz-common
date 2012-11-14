/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 * created on 01.11.2012 at 15:51:59
 * 
 *  Contributors:
 *      Till Kolditz
 *******************************************************************************/
package de.kolditz.common.ui.fields;

import java.util.Collection;
import java.util.Map.Entry;

/**
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public class MultiTextChoiceField<E> extends AbstractField<Entry<E, String>>
{
    protected Collection<String> labels;
    protected Collection<E> values;

    /**
     * @param parent
     * @param style
     * @param labelText
     * @param labels
     * @param types
     * @param initialValues
     */
    public MultiTextChoiceField(FieldComposite parent, int style, String labelText, Collection<String> labels,
            Collection<E> types, Collection<E> initialValues)
    {
        super(parent, style, labelText);

        create();
        setLabels();
        addListeners();
    }

    @Override
    protected void create()
    {
    }

    @Override
    protected void setLabels()
    {
    }

    @Override
    protected void addListeners()
    {
        // TODO Auto-generated method stub

    }

    @Override
    protected int getColumnsRequired()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected void setColumns(int columns)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setEnabled(boolean enabled)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean getEnabled()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Entry<E, String> getValue()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Entry<E, String> setValue(Entry<E, String> value, boolean doNotifyObservers)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isDisposed()
    {
        // TODO Auto-generated method stub
        return false;
    }
}
