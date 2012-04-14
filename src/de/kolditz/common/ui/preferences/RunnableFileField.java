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

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * A {@link FileField} that points to an executable file and contains an additional button for running it.
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public class RunnableFileField extends FileField {
    protected Button btnRun;

    /**
     * @param parent
     * @param style
     * @param label
     */
    public RunnableFileField(Composite parent, int style, String label) {
        super(parent, style, label);
    }

    public RunnableFileField(Composite parent, int style, String label, String null_hint) {
        super(parent, style, label, null_hint);
    }

    @Override
    protected void create() {
        super.create();
        ++((GridLayout) getLayout()).numColumns;

        btnRun = new Button(this, SWT.PUSH);
    }

    @Override
    protected void setLabels() {
        super.setLabels();
        btnRun.setText("Run");
    }

    @Override
    protected void addListeners() {
        super.addListeners();
        btnRun.addSelectionListener(this);
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
        if (e.widget == btnRun) {
            if (!cdFile.isVisible()) {
                try {
                    Runtime.getRuntime().exec(text.getText());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } else {
            super.widgetSelected(e);
        }
    }
}
