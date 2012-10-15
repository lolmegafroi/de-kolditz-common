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

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;

/**
 * A {@link FileField} that points to an executable file and contains an additional button for running it.
 * 
 * @author Till Kolditz - Till.Kolditz@gmail.com
 */
public class RunnableFileField extends FileField
{
    protected Button btnRun;

    /**
     * @param parent
     * @param style
     * @param label
     */
    public RunnableFileField(FieldComposite parent, int style, String label)
    {
        super(parent, style, label);
    }

    public RunnableFileField(FieldComposite parent, int style, String label, String null_hint)
    {
        super(parent, style, label, null_hint);
    }

    @Override
    protected void create()
    {
        super.create();
        btnRun = new Button(getComposite(), SWT.PUSH);
    }

    @Override
    protected void setLabels()
    {
        super.setLabels();
        btnRun.setText("Run");
    }

    @Override
    protected void addListeners()
    {
        super.addListeners();
        btnRun.addSelectionListener(new SelectionListener()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                RunnableFileField.this.widgetSelected(e);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
                RunnableFileField.this.widgetDefaultSelected(e);
            }
        });
    }

    @Override
    protected int getColumnsRequired()
    {
        return super.getColumnsRequired() + 1;
    }

    @Override
    protected void setColumns(int columns)
    {
        super.setColumns(columns - 1);
    }

    @Override
    public void widgetSelected(SelectionEvent e)
    {
        if(e.widget == btnRun)
        {
            if(!cdFile.isVisible())
            {
                try
                {
                    Runtime.getRuntime().exec(text.getText());
                }
                catch(IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        }
        else
        {
            super.widgetSelected(e);
        }
    }
}
