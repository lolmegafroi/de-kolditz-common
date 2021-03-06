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
package de.kolditz.common.ui.fields;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;

/**
 * @author Till Kolditz - Till.Kolditz@gmail.com
 */
public class LabelField extends AbstractField<String>
{
    protected boolean indented = false;
    protected Label label = null;
    protected int widthHint = SWT.DEFAULT;

    /**
     * When you want wrapping behavior, you HAVE to set a width hint (via {@link #setWidthHint(int)}).
     * 
     * @param parent
     *            the parent FieldComposite
     * @param style
     *            the {@link Label}'s style
     * @param labelText
     *            the Label's text
     * @param indented
     *            when <code>true</code>, the text will be shown underneath other fields' widgets, otherwise the label
     *            will stertch over the whole width
     */
    public LabelField(FieldComposite parent, int style, String labelText, boolean indented)
    {
        super(parent, style, labelText);

        this.indented = indented;
        create();
        addListeners();
        setLabels();
        parent.registerField(this);
    }

    @Override
    protected void create()
    {
        if (indented)
            new Label(getComposite(), SWT.NONE);
        label = new Label(getComposite(), style);
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
        return indented ? 2 : 1;
    }

    @Override
    protected void setColumns(int columns)
    {
        ((GridData) label.getLayoutData()).horizontalSpan = indented ? columns - 1 : columns;
    }

    @Override
    public void setEnabled(boolean enabled)
    {
    }

    @Override
    public boolean getEnabled()
    {
        return true;
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
        if (doNotifyObservers)
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

    /**
     * May be SWT.DEFAULT for disabling wrapping behavior.
     * 
     * @param widthHint
     */
    public void setWidthHint(int widthHint)
    {
        this.widthHint = Math.max(SWT.DEFAULT, widthHint); // at least SWT.DEFAULT
        if (label.getLayoutData() != null)
        {
            ((GridData) label.getLayoutData()).widthHint = this.widthHint;
        }
    }

    /**
     * Sets a layout data to this label field.
     * 
     * @param layoutData
     */
    public void setLayoutData(GridData layoutData)
    {
        label.setLayoutData(layoutData);
    }
}
