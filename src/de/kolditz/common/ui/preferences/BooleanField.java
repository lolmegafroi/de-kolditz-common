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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import de.kolditz.common.IObservableBackend;
import de.kolditz.common.ui.GetInUIThread.GetSelection;
import de.kolditz.common.ui.SetInUIThread.SetSelection;

/**
 * Preferences Boolean field
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public class BooleanField extends PreferenceField<Boolean> {
    protected String label;
    protected Button button;
    protected boolean doUpdateBackEnd;
    protected GetSelection getter;
    protected SetSelection setter;

    /**
     * @param parent
     * @param style
     */
    public BooleanField(Composite parent, int style, String label) {
        super(parent, style);
        this.label = label;
        create();
        setLabels();
        addListeners();
        setter = new SetSelection(button);
    }

    protected void create() {
        button = new Button(this, SWT.CHECK);
    }

    @Override
    protected void setLabels() {
        button.setText(label);
    }

    @Override
    protected void addListeners() {
        button.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    @Override
    public Boolean getValue() {
        return getter.get();
    }

    @Override
    public Boolean setValue(Boolean value, boolean doNotifyObservers) {
        Boolean old = getValue();
        Boolean actualVal = value;
        if (actualVal == null) {
            value = Boolean.FALSE;
        }
        setter.setValue(getDisplay(), actualVal);
        return old;
    }

    @Override
    protected IObservableBackend<Boolean> createBackend() {
        return new IObservableBackend<Boolean>(this);
    }
}
