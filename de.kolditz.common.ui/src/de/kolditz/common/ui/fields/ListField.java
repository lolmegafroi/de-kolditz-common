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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.List;

import de.kolditz.common.ui.GetInUIThread;
import de.kolditz.common.ui.SetInUIThread;

/**
 * A list field. Clients may choose between {@link SWT#SINGLE single} and {@link SWT#MULTI multi} selection.
 *
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public class ListField<E> extends AbstractListField<E, List>
{
    protected boolean singleSelection;
    protected SetInUIThread<E> sSetter = new SetInUIThread<E>()
    {
        @Override
        public void run()
        {
            if(value != null)
            {
                listControl.setSelection(new String[] {labelProvider.getText(value)});
            }
            else
            {
                listControl.deselectAll();
            }
        }
    };
    protected SetInUIThread<Collection<E>> mSetter = new SetInUIThread<Collection<E>>()
    {
        @Override
        public void run()
        {
            if(value != null && value.size() == 0)
            {
                int size = value.size();
                String[] strings = new String[size];
                int i = 0;
                for(E e : value)
                {
                    strings[i++] = labelProvider.getText(e);
                }
                listControl.setSelection(strings);
            }
            else
            {
                listControl.deselectAll();
            }
        };
    };
    protected GetInUIThread<E> sGetter = new GetInUIThread<E>()
    {
        @Override
        public void run()
        {
            int idx = listControl.getSelectionIndex();
            if(((style & SWT.MULTI) == 0 || listControl.getSelectionCount() == 1) && idx >= 0)
            {
                value = backingList.get(idx);
            }
            else
            {
                value = null;
            }
        }
    };
    protected GetInUIThread<Collection<E>> mGetter = new GetInUIThread<Collection<E>>()
    {
        @Override
        public void run()
        {
            int count = listControl.getSelectionCount();
            value = new ArrayList<E>(count);
            int[] indices = listControl.getSelectionIndices();
            for(int i = 0; i < count; ++i)
                value.add(backingList.get(indices[i]));
        }
    };

    /**
     * 
     * @param parent the parent FieldComposite
     * @param listStyle only one of {@link SWT#SINGLE} and {@link SWT#MULTI} may be specified
     * @param labelText the label's text
     * @param items the items to display
     * @param labelProvider a label provider, may be null (then a default one will be used)
     */
    public ListField(FieldComposite parent, int listStyle, String labelText, ILabelProvider labelProvider)
    {
        this(parent, listStyle, labelText, labelProvider, null);
    }

    /**
     * 
     * @param parent the parent FieldComposite
     * @param listStyle only one of {@link SWT#SINGLE} and {@link SWT#MULTI} may be specified
     * @param labelText the label's text
     * @param labelProvider a label provider, may be null (then a default one will be used)
     * @param items the items to display
     */
    public ListField(FieldComposite parent, int listStyle, String labelText, ILabelProvider labelProvider,
            Collection<E> items)
    {
        super(parent, listStyle, labelText, labelProvider, items);
        singleSelection = (listStyle & SWT.MULTI) == 0;
    }

    @Override
    protected List createListControl()
    {
        return new List(getComposite(), style);
    }

    @Override
    protected void addListeners()
    {
        listControl.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                if(singleSelection)
                {
                    notifyObservers(getValue());
                }
                else
                {
                    notifyObservers(getValues());
                }
            }
        });
    }

    /**
     * @return the number of selected items
     */
    public int getSelectionCount()
    {
        return listControl.getSelectionCount();
    }

    /**
     * Only returns non-null when a single element is selected
     */
    @Override
    public E getValue()
    {
        return sGetter.get(listControl.getDisplay());
    }

    @Override
    public Collection<E> getValues()
    {
        return mGetter.get(listControl.getDisplay());
    }

    @Override
    public E setValue(E value, boolean doNotifyObservers)
    {
        E old = getValue();
        sSetter.setValue(listControl.getDisplay(), value, false);
        if(doNotifyObservers)
        {
            notifyObservers(value);
        }
        return old;
    }

    public Collection<E> setValues(Collection<E> values, boolean doNotifyObservers)
    {
        Collection<E> old = getValues();
        mSetter.setValue(listControl.getDisplay(), values, false);
        if(doNotifyObservers)
        {
            notifyObservers(values);
        }
        return old;
    }

    @Override
    protected void updateList()
    {
        String[] items = new String[backingList.size()];
        int i = 0;
        for(E e : backingList)
        {
            items[i++] = labelProvider.getText(e);
        }
        listControl.setItems(items);
    }
}
