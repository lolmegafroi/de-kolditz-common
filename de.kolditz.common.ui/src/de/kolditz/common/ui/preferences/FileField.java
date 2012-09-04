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

import java.io.File;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;

import de.kolditz.common.util.SystemProperties;

/**
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public class FileField extends TextField implements SelectionListener, ModifyListener {
    private Logger log = Logger.getLogger(getClass());
    protected Button btnSet, btnClear;
    protected ControlDecoration cdFile;
    private String[] extensions, names;
    private int filterIndex;
    private boolean fileExists;

    /**
     * @param parent
     *            the parent {@link Composite}
     * @param style
     *            the parent Composite's style
     * @param label
     *            the {@link Label}'s text
     */
    public FileField(PreferencesComposite parent, int style, String label) {
        this(parent, style, label, "");
    }

    /**
     * @param parent
     *            the parent {@link Composite}
     * @param style
     *            the parent Composite's style
     * @param label
     *            the {@link Label}'s text
     * @param null_hint
     *            the hint text that is displayed when text field is empty
     */
    public FileField(PreferencesComposite parent, int style, String label, String null_hint) {
        super(parent, style, label, null_hint);
        setFilter(null, null, -1);
    }

    /**
     * Error-friendly set this FileField's filter extensions, names and initial index. If there are any errors (e.g.
     * different array lengths) the implementation tries to fix these issues by using extensions as names if necessary.
     * A wrong index will also be set to 0 or -1 depending on extensions and names. If no extensions but names are
     * provided the method will log an error but will behave as if no names were provided.
     * 
     * @param extensions
     * @param names
     * @param index
     */
    public void setFilter(String[] extensions, String[] names, int index) {
        if (extensions == null) {
            if (names != null) {
                log.error("No extensions but names are provided. Extensions = " + names.toString());
                names = null;
            }
            index = -1;
        } else if (names == null) {
            names = extensions;
        } else if (names.length != extensions.length) {
            // names and extensions != null
            if (names.length > extensions.length) {
                names = Arrays.copyOf(names, extensions.length);
                log.warn("More names than extensions provided. Cutting off the additional names.");
            } else {
                int oldLength = names.length;
                names = Arrays.copyOf(names, extensions.length);
                System.arraycopy(extensions, oldLength, names, oldLength, extensions.length - oldLength);
                log.warn("Less names than extensions provided. Using extensions as names.");
            }
        }
        this.extensions = extensions;
        this.names = names;
        this.filterIndex = index;
    }

    @Override
    protected void create() {
        super.create();
        Composite comp = getComposite();
        cdFile = new ControlDecoration(text, SWT.TOP | SWT.RIGHT, comp);
        cdFile.setDescriptionText("The file does not exist");
        cdFile.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_WARNING)
                .getImage());
        cdFile.hide();
        btnSet = new Button(comp, SWT.PUSH);
        btnClear = new Button(comp, SWT.PUSH);
    }

    @Override
    protected void setLabels() {
        super.setLabels();
        btnSet.setText("Set");
        btnClear.setText("Clear");
    }

    @Override
    protected void addListeners() {
        super.addListeners();
        btnSet.addSelectionListener(this);
        btnClear.addSelectionListener(this);
    }

    @Override
    protected int getColumnsRequired() {
        return super.getColumnsRequired() + 2;
    }

    @Override
    protected void setColumns(int columns) {
        super.setColumns(columns - 2);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (btnSet != null && !btnSet.isDisposed())
            btnSet.setEnabled(enabled);
        if (btnClear != null && !btnClear.isDisposed())
            btnClear.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
        if (e.widget == btnSet) {
            FileDialog fd = new FileDialog(text.getShell(), SWT.OPEN);
            String filterPath = text.getText();
            String fileName = "";
            if (filterPath.equals(null_hint)) {
                filterPath = SystemProperties.USER_DIR;
            } else {
                File file = new File(filterPath);
                if (file.exists()) {
                    if (file.isFile()) {
                        filterPath = file.getAbsoluteFile().getParent();
                        fileName = file.getName();
                    } else {
                        filterPath = file.getAbsolutePath();
                    }
                }
            }
            fd.setFilterPath(filterPath);
            fd.setFileName(fileName);
            fd.setFilterExtensions(extensions);
            fd.setFilterNames(names);
            if (filterIndex >= 0)
                fd.setFilterIndex(filterIndex);
            String target = fd.open();
            if (target != null) {
                text.setText(target);
                notifyObservers(target);
            }
        } else if (e.widget == btnClear) {
            text.setText(null_hint);
        }
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
    }

    @Override
    public void modifyText(ModifyEvent e) {
        if (e.widget == text) {
            File file = new File(text.getText());
            fileExists = file.exists();
            if (fileExists) {
                cdFile.hide();
            } else {
                cdFile.show();
            }
        }
        super.modifyText(e);
    }

    public boolean fileExists() {
        return fileExists;
    }
}
