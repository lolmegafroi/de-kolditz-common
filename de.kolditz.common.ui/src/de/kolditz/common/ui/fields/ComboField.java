/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 * created on 17.10.2012 at 17:33:30
 * 
 *  Contributors:
 *      Till Kolditz
 *******************************************************************************/
package de.kolditz.common.ui.fields;

import java.util.Collection;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;

/**
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public class ComboField<E> extends AbstractListField<E, Combo>
{
    /**
     * @param parent
     *            the parent FieldComposite
     * @param comboStyle
     *            {@link SWT#DROP_DOWN}, {@link SWT#READ_ONLY}, {@link SWT#SIMPLE}
     * @param labelText
     *            the label's text
     * @param items
     *            the items to display
     * @param labelProvider
     *            a label provider, may be null (then a default one will be used)
     */
    public ComboField(FieldComposite parent, int comboStyle, String labelText, Collection<E> items,
            ILabelProvider labelProvider)
    {
        super(parent, comboStyle, labelText, labelProvider, items);
    }

    @Override
    protected Combo createListControl()
    {
        return new Combo(getComposite(), style);
    }

    @Override
    public int getSelectionCount()
    {
        return 1;
    }

    @Override
    protected void addListeners()
    {
        listControl.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                notifyObservers(getValue());
            }
        });
    }

    @Override
    public E getValue()
    {
        int idx = listControl.getSelectionIndex();
        return idx >= 0 ? backingList.get(idx) : null;
    }

    @Override
    public E setValue(E value, boolean doNotifyObservers)
    {
        E old = getValue();
        if (value != null)
        {
            for (int idx = 0; idx < backingList.size(); ++idx)
            {
                if (backingList.get(idx).equals(value))
                    listControl.select(idx);
            }
        }
        else
        {
            listControl.deselectAll();
        }
        return old;
    }

    @Override
    protected void updateList()
    {
        String[] items = new String[backingList.size()];
        int i = 0;
        for (E e : backingList)
        {
            items[i] = labelProvider.getText(e);
        }
        listControl.setItems(items);
    }
}
