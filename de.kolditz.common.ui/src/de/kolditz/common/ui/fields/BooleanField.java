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
package de.kolditz.common.ui.fields;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;

import de.kolditz.common.concurrent.MultiThreaded;
import de.kolditz.common.ui.concurrent.GetInUIThread.GetSelection;
import de.kolditz.common.ui.concurrent.SetInUIThread.SetSelection;

/**
 * Boolean button field. Allows to set a description text. If this one was created without a description text, one may
 * still be added using {@link #setDescription(String)} in which case the description label will be added and the
 * parent's layout updated. The description text is selectable.
 * 
 * @author Till Kolditz - Till.Kolditz@gmail.com
 */
public class BooleanField extends AbstractField<Boolean>
{
    protected SelectionListener buttonListener = null;

    protected MouseListener labelListener;

    protected Label label = null;
    protected Button button = null;
    protected boolean separateLabel = false;
    protected boolean asControlDecoration = false;
    protected boolean doUpdateBackEnd = false;
    protected GetSelection getter = null;
    protected SetSelection setter = null;
    protected String description = null;
    protected ControlDecoration cd = null;
    protected Label lbDescription;

    /**
     * @param parent
     *            the parent {@link FieldComposite}
     * @param style
     *            ignored for now
     * @param labelString
     *            the {@link Label}'s text
     * @param separateLabel
     *            whether to create a separate label widget
     */
    public BooleanField(FieldComposite parent, int style, String labelString, boolean separateLabel)
    {
        this(parent, style, labelString, separateLabel, null, true);
    }

    /**
     * @param parent
     *            the parent {@link FieldComposite}
     * @param style
     *            ignored for now
     * @param labelString
     *            the {@link Label}'s text
     * @param separateLabel
     *            whether to create a separate label widget
     * @param descriptionString
     *            an additional description
     * @param asControlDecoration
     *            whether to show the description as a {@link ControlDecoration} (true) or an additional label (false)
     */
    public BooleanField(FieldComposite parent, int style, String labelString, boolean separateLabel,
            String descriptionString, boolean asControlDecoration)
    {
        super(parent, style, labelString);

        this.separateLabel = separateLabel;
        description = descriptionString;
        this.asControlDecoration = asControlDecoration;

        create();
        setLabels();
        addListeners();
        parent.registerField(this);
    }

    protected void create()
    {
        if (button == null)
        {
            if (separateLabel)
                label = new Label(getComposite(), SWT.NONE);
            button = new Button(getComposite(), SWT.CHECK);
            button.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        }
        if (description != null)
        {
            if (asControlDecoration)
            {
                if (cd == null)
                {
                    cd = new ControlDecoration(button, SWT.TOP | SWT.LEFT, button.getShell());
                    cd.setImage(FieldDecorationRegistry.getDefault()
                            .getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION).getImage());
                    cd.setMarginWidth(1);
                    cd.show();
                }
            }
            else
            {
                if (lbDescription == null)
                {
                    if (!separateLabel)
                        new Label(getComposite(), SWT.NONE);
                    lbDescription = new Label(getComposite(), SWT.NONE);
                    lbDescription.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
                    ((GridData) button.getLayoutData()).grabExcessHorizontalSpace = false;
                }
            }
        }
    }

    @Override
    protected void setLabels()
    {
        if (separateLabel)
            label.setText(labelText);
        else
            button.setText(labelText);
        if (asControlDecoration && cd != null)
            cd.setDescriptionText(description);
        else if (lbDescription != null)
            lbDescription.setText(description);
    }

    @Override
    protected void addListeners()
    {
        if (setter == null)
            setter = new SetSelection(button);
        if (getter == null)
            getter = new GetSelection(button);

        if (buttonListener == null)
        {
            buttonListener = new SelectionListener()
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
            };
            button.addSelectionListener(buttonListener);
        }
        if (!asControlDecoration && labelListener == null)
        {
            labelListener = new MouseAdapter()
            {
                @Override
                public void mouseUp(MouseEvent e)
                {
                    if (button != null && !button.isDisposed())
                    {
                        boolean value = getter.get(button.getDisplay());
                        setter.setValue(button.getDisplay(), value);
                        notifyObservers(value);
                    }
                }
            };
            lbDescription.addMouseListener(labelListener);
        }
    }

    @Override
    protected int getColumnsRequired()
    {
        int columns = 1;
        if (separateLabel)
            ++columns;
        if (description != null && !asControlDecoration && separateLabel)
            ++columns;
        return columns;
    }

    @Override
    protected void setColumns(int columns)
    {
        int cols = separateLabel ? columns - 1 : columns;
        if (description != null && !asControlDecoration && lbDescription != null)
        {
            if (separateLabel) // lbDescription is on the same line
                --cols;
            else
                // lbDescription is on the full second line
                ((GridData) lbDescription.getLayoutData()).horizontalSpan = cols;
        }
        ((GridData) button.getLayoutData()).horizontalSpan = cols;
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        if (separateLabel & label != null)
            label.setEnabled(false);
        if (description != null)
        {
            if (!asControlDecoration && lbDescription != null)
                lbDescription.setEnabled(enabled);
            else if (asControlDecoration && cd != null)
            {
                if (enabled)
                    cd.show();
                else
                    cd.hide();
            }
        }
        button.setEnabled(enabled);
    }

    @Override
    public boolean getEnabled()
    {
        return button.getEnabled();
    }

    @MultiThreaded
    @Override
    public Boolean getValue()
    {
        return getter.get(button.getDisplay());
    }

    @MultiThreaded
    @Override
    public Boolean setValue(Boolean value, boolean doNotifyObservers)
    {
        Boolean old = getValue();
        Boolean actualVal = value;
        if (actualVal == null)
        {
            actualVal = Boolean.FALSE;
        }
        setter.setValue(button.getDisplay(), actualVal);
        if (doNotifyObservers)
            notifyObservers(actualVal);
        return old;
    }

    @Override
    public boolean isDisposed()
    {
        return button.isDisposed();
    }

    public void setDescription(String descriptionText)
    {
        this.description = descriptionText;
        if (description == null)
        {
            // either description is set to null or lbDescription is not yet created
            if (cd != null) // description != null
            {
                cd.hide();
            }
        }
        else
        {
            if (cd == null && lbDescription == null)
            {
                create();
                addListeners();
            }
            setLabels();
        }
    }
}
