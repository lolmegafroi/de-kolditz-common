/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 * created on 17.10.2012 at 16:43:05
 * 
 *  Contributors:
 *      Till Kolditz
 *******************************************************************************/
package de.kolditz.common.ui.fields;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * The abstract list field. Implementors MUST call the super methods! Implementors may use the field {@link #listControl}
 * for accessing the actual list control. This base implementation assumes that only one additional control is created,
 * next to the label. Subclasses which create more widgets should override {@link #getColumnsRequired()} and
 * {@link #setColumns(int)}.
 *
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public abstract class AbstractListField<E, C extends Control> extends AbstractField<E>
{
    protected Label label;
    protected C listControl;
    protected ArrayList<E> backingList;
    protected ILabelProvider labelProvider;

    /**
     * 
     * @param parent the parent FieldComposite
     * @param listStyle depends on the actual list type
     * @param labelText the label's text
     * @param items the items to display
     * @param labelProvider a label provider, may be null (then a default one will be used)
     */
    public AbstractListField(FieldComposite parent, int listStyle, String labelText, ILabelProvider labelProvider,
            Collection<E> items)
    {
        super(parent, listStyle, labelText);
        backingList = new ArrayList<E>();
        if(items != null) backingList.addAll(items);
        this.labelProvider = labelProvider != null ? labelProvider : new LabelProvider();

        create();
        updateList();
        addListeners();
        setLabels();
        parent.registerField(this);
    }

    @Override
    protected void create()
    {
        label = new Label(getComposite(), SWT.NONE);
        listControl = createListControl();
        listControl.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
    }

    protected abstract C createListControl();

    @Override
    protected void setLabels()
    {
        label.setText(labelText);
        String[] items = new String[backingList.size()];
        int i = 0;
        for(E item : backingList)
        {
            items[i++] = labelProvider.getText(item);
        }
    }

    @Override
    protected int getColumnsRequired()
    {
        return 2;
    }

    @Override
    protected void setColumns(int columns)
    {
        ((GridData)listControl.getLayoutData()).horizontalSpan = columns - 1;
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        listControl.setEnabled(enabled);
    }

    @Override
    public boolean getEnabled()
    {
        return listControl.getEnabled();
    }

    /**
     * @return the number of selected items
     */
    public abstract int getSelectionCount();

    @Override
    public boolean isDisposed()
    {
        return listControl.isDisposed();
    }

    /**
     * Updates the list control's items from the {@link #backingList}.
     */
    protected abstract void updateList();

    public void setItems(Collection<E> items)
    {
        backingList.clear();
        if(items != null) backingList.addAll(items);
        updateList();
    }

    public void setItems(E[] items)
    {
        backingList.clear();
        if(items != null && items.length > 0)
        {
            backingList.ensureCapacity(items.length);
            for(E e : items)
                backingList.add(e);
        }
        updateList();
    }
}
