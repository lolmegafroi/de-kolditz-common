/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 * created on 13.07.2012 at 12:37:14
 * 
 *  Contributors:
 *      Till Kolditz
 *******************************************************************************/
package de.kolditz.common.ui.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;

/**
 * 
 * @author Till Kolditz - Till.Kolditz@gmail.com
 */
public class LabelField extends PreferenceField<String>
{
    protected Label label;
    protected int labelStyle;
    protected String labelText;

    /**
     * @param parent
     * @param style
     */
    public LabelField(PreferencesComposite parent, int style, int labelStyle, String labelText)
    {
        super(parent, style);

        assert labelText != null : new NullPointerException("labelText"); //$NON-NLS-1$

        this.labelStyle = labelStyle;
        this.labelText = labelText;

        create();
        setLabels();
        addListeners();
    }

    @Override
    protected void create()
    {
        label = new Label(getComposite(), labelStyle);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
    }

    @Override
    protected void setLabels()
    {
        label.setText(labelText);
    }

    @Override
    protected void addListeners()
    {
    }

    @Override
    protected int getColumnsRequired()
    {
        return 1;
    }

    @Override
    protected void setColumns(int columns)
    {
        ((GridData)label.getLayoutData()).horizontalSpan = columns;
    }

    @Override
    public void setEnabled(boolean enabled)
    {
    }

    @Override
    public String getValue()
    {
        return labelText;
    }

    @Override
    public String setValue(String value, boolean doNotifyObservers)
    {
        String old = labelText;
        labelText = value;
        label.setText(labelText);
        if(doNotifyObservers)
        {
            notifyObservers(labelText);
        }
        return old;
    }

    @Override
    public boolean isDisposed()
    {
        return label.isDisposed();
    }
}
