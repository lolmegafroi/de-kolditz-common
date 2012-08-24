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
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.kolditz.common.ui.GetInUIThread.GetText;
import de.kolditz.common.ui.SetInUIThread.SetText;
import de.kolditz.common.util.Pair;

/**
 * Preferences Text field
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public class TextField extends PreferenceField<String> implements ModifyListener, FocusListener {
    protected Label label;
    protected Text text;
    protected String labelString;
    protected String null_hint;
    protected boolean doUpdateBackEnd;
    protected Pair<String, String> pair;
    protected SetText setter;
    protected GetText getter;

    /**
     * @param parent
     *            parent Composite
     * @param style
     *            Composite style
     * @param label
     *            label text
     */
    public TextField(PreferencesComposite parent, int style, String label) {
        this(parent, style, label, "");
    }

    /**
     * @param parent
     *            parent Composite
     * @param style
     *            Composite style
     * @param label
     *            label text
     * @param null_hint
     *            hint text shown when no text is entered
     */
    public TextField(PreferencesComposite parent, int style, String label, String null_hint) {
        super(parent, style);
        labelString = label;
        this.null_hint = null_hint != null ? null_hint : ""; //$NON-NLS-1$

        create();
        addListeners();
        setLabels();

        doUpdateBackEnd = true;
        pair = new Pair<String, String>(null, null);
        setter = new SetText(text);
        getter = new GetText(text);
    }

    protected void create() {
        label = new Label(getComposite(), SWT.NONE);
        text = new Text(getComposite(), SWT.BORDER);
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    }

    protected void setLabels() {
        doUpdateBackEnd = false;
        label.setText(labelString);
        text.setText(null_hint);
        doUpdateBackEnd = true;
    }

    protected void addListeners() {
        text.addModifyListener(this);
        text.addFocusListener(this);
    }

    @Override
    protected int getColumnsRequired() {
        return 2;
    }

    @Override
    protected void setColumns(int columns) {
        ((GridData) text.getLayoutData()).horizontalSpan = columns - 1;
    }

    @Override
    public void modifyText(ModifyEvent e) {
        if (text.getText().equals(null_hint)) {
            text.setForeground(text.getDisplay().getSystemColor(SWT.COLOR_GRAY));
        } else {
            text.setForeground(text.getDisplay().getSystemColor(SWT.COLOR_BLACK));
        }
        if (doUpdateBackEnd) {
            notifyObservers(getValue());
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        doUpdateBackEnd = false;
        if (text.getText().equals(null_hint)) {
            text.setText("");
        }
        doUpdateBackEnd = true;
    }

    @Override
    public void focusLost(FocusEvent e) {
        doUpdateBackEnd = false;
        if (text.getText().equals("")) {
            text.setText(null_hint);
        }
        doUpdateBackEnd = true;
    }

    public String setValue(String value, boolean doNotifyObservers) {
        String old = getValue();
        String actualVal = value;
        if (value == null || value.length() == 0) {
            actualVal = null_hint;
        }
        doUpdateBackEnd = doNotifyObservers;
        setter.setValue(text.getDisplay(), pair.first(actualVal).second(actualVal), false);
        doUpdateBackEnd = true;
        return old;
    }

    public String getValue() {
        String str = getter.get();
        if (str.equals(null_hint)) {
            return null;
        }
        return str;
    }

    @Override
    public void setEnabled(boolean enabled) {
        text.setEnabled(enabled);
    }
}
