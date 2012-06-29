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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * A {@link PreferenceField} containing several {@link Button}s.
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public class MultiButtonField<E> extends PreferenceField<E> {
    protected int groupStyle;
    protected String label;
    protected int columns;
    protected E[] values;
    protected String[] labels;
    protected Group group;
    protected Button[] buttons;

    /**
     * @param parent
     *            this widget's parent {@link Composite}
     * @param groupStyle
     *            this {@link Composite}'s style
     * @param buttonStyle
     *            the style of the created {@link Button}s
     * @param columns
     *            the number of columns in which the buttons are aligned (e.g. 1=vertical or values.length=horizontal)
     * @param values
     *            the values associated with each button
     * @param labels
     *            the buttons' text labels
     */
    public MultiButtonField(Composite parent, int groupStyle, String label, int buttonStyle, int columns, E[] values,
            String[] labels) {
        super(parent, SWT.NONE);

        assert values != null : new IllegalArgumentException("values = null"); //$NON-NLS-1$
        assert values.length > 0 : new IllegalArgumentException("values.length = 0"); //$NON-NLS-1$
        assert labels != null : new IllegalArgumentException("labels = null"); //$NON-NLS-1$
        assert values.length == labels.length : new IllegalArgumentException("values.length != labels.length"); //$NON-NLS-1$

        this.groupStyle = buttonStyle;

        this.label = label;
        this.columns = columns;
        this.values = values;
        this.labels = labels;

        create();
        setLabels();
        addListeners();
    }

    @Override
    protected void create() {
        GridLayout gl = new GridLayout(1, false);
        gl.marginHeight = 0;
        gl.marginWidth = 0;
        setLayout(gl);

        group = new Group(this, groupStyle);
        group.setLayout(new GridLayout(columns, false));

        Button b;
        buttons = new Button[values.length];
        for (int i = 0; i < values.length; ++i) {
            b = new Button(group, groupStyle);
            b.setData(values[i]);
            buttons[i] = b;
        }
    }

    @Override
    protected void setLabels() {
        if (label != null) {
            group.setText(label);
        }
        for (int i = 0; i < values.length; ++i) {
            buttons[i].setText(labels[i]);
        }
    }

    @Override
    protected void addListeners() {
        for (int i = 0; i < values.length; ++i) {
            buttons[i].addSelectionListener(new SelectionAdapter() {
                @SuppressWarnings("unchecked")
                @Override
                public void widgetSelected(SelectionEvent e) {
                    notifyObservers((E) ((Button) e.widget).getData());
                }
            });
        }
    }

    @Override
    public E getValue() {
        for (int i = 0; i < values.length; ++i) {
            if (buttons[i].getSelection()) {
                return values[i];
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E[] getValues() {
        ArrayList<E> values = new ArrayList<E>(this.values.length);
        for (int i = 0; i < buttons.length; ++i) {
            if (buttons[i].getSelection()) {
                values.add((E) buttons[i].getData());
            }
        }
        return values.toArray((E[]) Array.newInstance(this.values[0].getClass(), values.size()));
    }

    @Override
    public E setValue(E value, boolean doNotifyObservers) {
        E oldValue = getValue();
        if (value == null) {
            for (int i = 0; i < values.length; ++i) {
                buttons[i].setSelection(false);
            }
        } else {
            for (int i = 0; i < values.length; ++i) {
                buttons[i].setSelection(values[i].equals(value));
            }
        }
        if (doNotifyObservers) {
            notifyObservers(value);
        }
        return oldValue;
    }

    @SuppressWarnings("unchecked")
    public E[] setValues(E[] values, boolean doNotifyObservers) {
        E[] oldValues = getValues();
        Button b;
        E value;
        boolean selection;
        for (int i = 0; i < buttons.length; ++i) {
            b = buttons[i];
            value = (E) b.getData();
            selection = false;
            for (int j = 0; j < values.length; ++j) {
                if (value.equals(values[j])) {
                    selection = true;
                    break;
                }
            }
            b.setSelection(selection);
        }
        return oldValues;
    };

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        group.setEnabled(enabled);
        for (Button b : buttons) {
            b.setEnabled(enabled);
        }
    }
}
