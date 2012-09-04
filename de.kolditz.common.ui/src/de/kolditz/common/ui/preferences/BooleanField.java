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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;

import de.kolditz.common.concurrent.MultiThreaded;
import de.kolditz.common.ui.GetInUIThread.GetSelection;
import de.kolditz.common.ui.SetInUIThread.SetSelection;

/**
 * Preferences Boolean field
 * 
 * @author Till Kolditz - Till.Kolditz@gmail.com
 */
public class BooleanField extends PreferenceField<Boolean>
{
    protected String label;
    protected Button button;
    protected boolean doUpdateBackEnd;
    protected GetSelection getter;
    protected SetSelection setter;

    /**
     * @param parent
     * @param style
     */
    public BooleanField(PreferencesComposite parent, int style, String label)
    {
        super(parent, style);
        this.label = label;

        create();
        setLabels();
        addListeners();

        setter = new SetSelection(button);
        getter = new GetSelection(button);
    }

    protected void create()
    {
        button = new Button(getComposite(), SWT.CHECK);
    }

    @Override
    protected void setLabels()
    {
        button.setText(label);
    }

    @Override
    protected void addListeners()
    {
        button.addSelectionListener(new SelectionListener()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                notifyObservers(button.getSelection());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
                notifyObservers(button.getSelection());
            }
        });
    }

    @Override
    protected int getColumnsRequired()
    {
        return 1;
    }

    @Override
    protected void setColumns(int columns)
    {
        button.setLayoutData(new GridData(SWT.LEAD, SWT.CENTER, false, false, columns, 1));
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        button.setEnabled(enabled);
    }

    @MultiThreaded
    @Override
    public Boolean getValue()
    {
        return getter.get();
    }

    @MultiThreaded
    @Override
    public Boolean setValue(Boolean value, boolean doNotifyObservers)
    {
        Boolean old = getValue();
        Boolean actualVal = value;
        if(actualVal == null)
        {
            actualVal = Boolean.FALSE;
        }
        setter.setValue(button.getDisplay(), actualVal);
        if(doNotifyObservers) notifyObservers(actualVal);
        return old;
    }

    @Override
    public boolean isDisposed()
    {
        return button.isDisposed();
    }
}
