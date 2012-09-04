/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Till Kolditz
 *******************************************************************************/
package de.kolditz.common.ui.preferences;

import java.lang.reflect.Array;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import de.kolditz.common.ui.ButtonBar;

/**
 * A {@link PreferenceField} containing several {@link Button}s.
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public class MultiButtonField<E> extends PreferenceField<E>
{
    protected int groupStyle;
    protected int buttonStyle;
    protected String labelString;
    protected int columns;
    protected E[] values;
    protected String[] labels;
    protected Label label;
    protected Group group;
    protected ButtonBar buttonBar;
    protected Button[] buttons;
    protected boolean asGroup;

    /**
     * @param parent
     *            this widget's parent {@link Composite}
     * @param asGroup
     *            whether this multi button field should be organized as a group or not
     * @param groupStyle
     *            this {@link Composite}'s style
     * @param label
     *            the label string
     * @param buttonStyle
     *            the style of the created {@link Button}s
     * @param columns
     *            the number of columns in which the buttons are aligned (e.g. 1=vertical or values.length=horizontal)
     * @param values
     *            the values associated with each button
     * @param labels
     *            the buttons' text labels
     */
    public MultiButtonField(PreferencesComposite parent, boolean asGroup, int groupStyle, String label,
            int buttonStyle, int columns, E[] values, String[] labels)
    {
        super(parent, SWT.NONE);

        assert values != null : new IllegalArgumentException("values = null"); //$NON-NLS-1$
        assert values.length > 0 : new IllegalArgumentException("values.length = 0"); //$NON-NLS-1$
        assert labels != null : new IllegalArgumentException("labels = null"); //$NON-NLS-1$
        assert values.length == labels.length : new IllegalArgumentException("values.length != labels.length"); //$NON-NLS-1$

        this.asGroup = asGroup;
        this.groupStyle = buttonStyle;

        this.buttonStyle = buttonStyle;

        this.labelString = label;
        this.columns = columns;
        this.values = values;
        this.labels = labels;

        create();
        setLabels();
        addListeners();
    }

    @Override
    protected void create()
    {
        Composite buttonComp;
        if(asGroup)
        {
            group = new Group(getComposite(), groupStyle);
            group.setLayoutData(new GridData(SWT.LEAD, SWT.CENTER, false, false));
            buttonComp = group;
        }
        else
        {
            label = new Label(getComposite(), SWT.NONE);
            buttonComp = getComposite();
        }

        buttonBar = new ButtonBar(buttonComp, SWT.NONE);
        buttonBar.setLayoutData(new GridData(SWT.LEAD, SWT.CENTER, false, false));
        Button b;
        buttons = new Button[values.length];
        for(int i = 0; i < values.length; ++i)
        {
            b = buttonBar.createButton(i, "", false, buttonStyle);
            b.setData(values[i]);
            buttons[i] = b;
        }
    }

    @Override
    protected int getColumnsRequired()
    {
        return asGroup ? 1 : columns + 1;
    }

    @Override
    protected void setColumns(int columns)
    {
        if(asGroup)
        {
            ((GridData)group.getLayoutData()).horizontalSpan = columns;
        }
        else
        {
            ((GridData)buttonBar.getLayoutData()).horizontalSpan = columns - 1;
        }
    }

    @Override
    protected void setLabels()
    {
        if(labelString != null)
        {
            if(asGroup)
            {
                group.setText(labelString);
            }
            else
            {
                label.setText(labelString);
            }
        }
        for(int i = 0; i < values.length; ++i)
        {
            buttons[i].setText(labels[i]);
        }
    }

    @Override
    protected void addListeners()
    {
        for(int i = 0; i < values.length; ++i)
        {
            buttons[i].addSelectionListener(new SelectionAdapter()
            {
                @SuppressWarnings("unchecked")
                @Override
                public void widgetSelected(SelectionEvent e)
                {
                    notifyObservers((E)((Button)e.widget).getData());
                }
            });
        }
    }

    @Override
    public E getValue()
    {
        for(int i = 0; i < values.length; ++i)
        {
            if(buttons[i].getSelection())
            {
                return values[i];
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E[] getValues()
    {
        ArrayList<E> values = new ArrayList<E>(this.values.length);
        for(int i = 0; i < buttons.length; ++i)
        {
            if(buttons[i].getSelection())
            {
                values.add((E)buttons[i].getData());
            }
        }
        return values.toArray((E[])Array.newInstance(this.values[0].getClass(), values.size()));
    }

    @Override
    public E setValue(E value, boolean doNotifyObservers)
    {
        E oldValue = getValue();
        if(value == null)
        {
            for(int i = 0; i < values.length; ++i)
            {
                buttons[i].setSelection(false);
            }
        }
        else
        {
            for(int i = 0; i < values.length; ++i)
            {
                buttons[i].setSelection(values[i].equals(value));
            }
        }
        if(doNotifyObservers)
        {
            notifyObservers(value);
        }
        return oldValue;
    }

    @SuppressWarnings("unchecked")
    public E[] setValues(E[] values, boolean doNotifyObservers)
    {
        E[] oldValues = getValues();
        Button b;
        E value;
        boolean selection;
        for(int i = 0; i < buttons.length; ++i)
        {
            b = buttons[i];
            value = (E)b.getData();
            selection = false;
            for(int j = 0; j < values.length; ++j)
            {
                if(value.equals(values[j]))
                {
                    selection = true;
                    break;
                }
            }
            b.setSelection(selection);
        }
        return oldValues;
    };

    @Override
    public void setEnabled(boolean enabled)
    {
        for(Button b : buttons)
        {
            b.setEnabled(enabled);
        }
    }

    @Override
    public boolean isDisposed()
    {
        return buttonBar.isDisposed();
    }
}
