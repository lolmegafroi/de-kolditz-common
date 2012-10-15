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

import java.io.File;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;

import de.kolditz.common.ui.i18n.I18N;
import de.kolditz.common.util.SystemProperties;

/**
 * A file field which allows to select an existing file, shows a control decoration and allows the file dialog's filter
 * to be set. A warning (control decoration) is shown when either the given file does not exist, or when no file is set
 * at all. The missing file warning will, however, not be shown when the style is {@link SWT#SAVE}.
 * <p>{@link SWT#MULTI} is not yet fully supported!
 * </p>
 * 
 * @author Till Kolditz - Till.Kolditz@gmail.com
 * @see #setFilter(String[], String[], int)
 * @see #setShowDecoration(boolean)
 * @see #setOverwriteAsk(boolean)
 */
public class FileField extends TextField
{
    private Logger log = Logger.getLogger(getClass());
    protected Button btnSet, btnClear;
    protected boolean isShowDecoration = true;
    protected boolean isOverwriteAsk = false;
    protected ControlDecoration cdFile;
    private String[] extensions, names;
    private int filterIndex;
    private boolean fileExists = false;
    private boolean doOverwrite = false;
    private SelectionListener selectionListener;

    /**
     * A file field without a text hint.
     * 
     * @param parent
     *            the parent {@link Composite}
     * @param style
     *            this field's style: either {@link SWT#OPEN} or {@link SWT#SAVE} (defaults to OPEN), and optionally {@link SWT#MULTI}
     * @param label
     *            the {@link Label}'s text
     * @see #setFilter(String[], String[], int)
     * @see #setShowDecoration(boolean)
     * @see #setOverwriteAsk(boolean)
     */
    public FileField(FieldComposite parent, int style, String label)
    {
        this(parent, style, label, ""); //$NON-NLS-1$
    }

    /**
     * A file field with a text hint.
     * 
     * @param parent
     *            the parent {@link Composite}
     * @param style
     *            this field's style: either {@link SWT#OPEN} or {@link SWT#SAVE} (defaults to OPEN), and optionally {@link SWT#MULTI}
     * @param label
     *            the {@link Label}'s text
     * @param null_hint
     *            the hint text that is displayed when text field is empty
     * @see #setFilter(String[], String[], int)
     * @see #setShowDecoration(boolean)
     */
    public FileField(FieldComposite parent, int style, String label, String null_hint)
    {
        super(parent, style, label, null_hint);
        setFilter(null, null, -1);
    }

    /**
     * Error-compensatingly set this FileField's filter extensions, names and initial index.
     * If there are any errors (e.g. different array lengths) the implementation tries to fix these issues by using
     * extensions as names if necessary. A wrong index will also be set to 0 or -1 depending on extensions and names.
     * If no extensions but names are provided the method will log an error but will behave as if no names were
     * provided.
     * <p>
     * 
     * TODO support for more complex extensions with more than 1 dot (e.g. "*.my.xml")
     * 
     * @param extensions
     *            a list of semicolon-separated extension lists, e.g. <code>new String[] {"*.txt;*.log", "*.bat"}</code>
     * @param names
     *            a list of strings conforming to the extensions, e.g. <code>new String[] {"Text-Files
     *            (*.txt;*.log)", "Batch-File (*.bat)"}</code>
     * @param index
     */
    public void setFilter(String[] extensions, String[] names, int index)
    {
        if(extensions == null)
        {
            if(names != null)
            {
                log.error("No extensions but names are provided. Extensions = " + names.toString());
                names = null;
            }
            index = -1;
        }
        else if(names == null)
        {
            names = extensions;
        }
        else if(names.length != extensions.length)
        {
            // names and extensions != null
            if(names.length > extensions.length)
            {
                names = Arrays.copyOf(names, extensions.length);
                log.warn("More names than extensions provided. Cutting off the additional names.");
            }
            else
            {
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
    protected void create()
    {
        super.create();
        Composite comp = getComposite();
        cdFile = new ControlDecoration(text, SWT.TOP | SWT.RIGHT, comp);
        cdFile.setDescriptionText(I18N.get().getString(I18N.FIELDS_FILEFIELD_FILENOTSET));
        cdFile.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_WARNING)
                .getImage());
        cdFile.hide();
        btnSet = new Button(comp, SWT.PUSH);
        GridData gd = new GridData();
        gd.horizontalIndent = 10;
        btnSet.setLayoutData(gd);
        btnClear = new Button(comp, SWT.PUSH);
    }

    @Override
    protected void setLabels()
    {
        super.setLabels();
        btnSet.setText("Set");
        btnClear.setText("Clear");
    }

    @Override
    protected void addListeners()
    {
        super.addListeners();
        selectionListener = new SelectionListener()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                FileField.this.widgetSelected(e);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
                FileField.this.widgetDefaultSelected(e);
            }
        };
        btnSet.addSelectionListener(selectionListener);
        btnClear.addSelectionListener(selectionListener);
    }

    @Override
    protected int getColumnsRequired()
    {
        return super.getColumnsRequired() + 2;
    }

    @Override
    protected void setColumns(int columns)
    {
        super.setColumns(columns - 2);
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        if(btnSet != null && !btnSet.isDisposed()) btnSet.setEnabled(enabled);
        if(btnClear != null && !btnClear.isDisposed()) btnClear.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    /**
     * TODO support for complex file filters
     * @param e
     */
    protected void widgetSelected(SelectionEvent e)
    {
        if(e.widget == btnSet)
        {
            FileDialog fd = new FileDialog(text.getShell(), ((style & SWT.OPEN) != 0) ? SWT.OPEN : SWT.SAVE);
            String filterPath = text.getText();
            String fileName = "";
            if(filterPath.equals(null_hint))
            {
                filterPath = SystemProperties.USER_DIR;
            }
            else
            {
                File file = new File(filterPath);
                if(file.exists())
                {
                    if(file.isFile())
                    {
                        filterPath = file.getAbsoluteFile().getParent();
                        fileName = file.getName();
                    }
                    else
                    {
                        filterPath = file.getAbsolutePath();
                    }
                }
            }
            fd.setFilterPath(filterPath);
            fd.setFileName(fileName);
            fd.setFilterExtensions(extensions);
            fd.setFilterNames(names);
            if(filterIndex >= 0) fd.setFilterIndex(filterIndex);
            // TODO multi-file selection support
            String target = fd.open();
            int filterIdx = fd.getFilterIndex();
            if(target != null)
            {
                boolean doIt = true;
                if(isOverwriteAsk)
                {
                    File file = new File(target);
                    fileExists = file.exists();
                    if(fileExists)
                    {
                        doIt = doOverwrite = MessageDialog.openQuestion(text.getShell(),
                                I18N.get().getString(I18N.FIELDS_FILEFIELD_OVERWRITEDIALOG_MSG),
                                I18N.get().getString(I18N.FIELDS_FILEFIELD_OVERWRITEDIALOG_MSG));
                    }
                }
                if(doIt)
                {
                    if(filterIdx >= 0)
                    {
                        // get (first) extension from the set filters
                        String ext = extensions[filterIdx];
                        if(ext.contains(";"))
                        {
                            boolean extPresent = false;
                            String[] tempExtensions = ext.split(";");
                            for(String extension : tempExtensions)
                            {
                                if(target.endsWith(extension.substring(extension.lastIndexOf('.'))))
                                {
                                    extPresent = true;
                                    break;
                                }
                            }
                            if(!extPresent)
                            {
                                target += tempExtensions[0].substring(tempExtensions[0].lastIndexOf('.'));
                            }
                        }
                        else
                        {
                            ext = ext.substring(ext.lastIndexOf('.'));
                            if(!target.endsWith(ext))
                            {
                                target += ext;
                            }
                        }
                    }
                    text.setText(target);
                    notifyObservers(target);
                }
            }
        }
        else if(e.widget == btnClear)
        {
            text.setText(null_hint);
        }
    }

    protected void widgetDefaultSelected(SelectionEvent e)
    {
    }

    @Override
    protected void modifyText(ModifyEvent e)
    {
        if(e.widget == text)
        {
            validate();
        }
        super.modifyText(e);
    }

    public boolean fileExists()
    {
        return fileExists;
    }

    public void validate()
    {
        if(isShowDecoration)
        {
            String txt = text.getText();
            if(txt.length() == 0)
            {
                cdFile.setDescriptionText(I18N.get().getString(I18N.FIELDS_FILEFIELD_FILENOTSET));
                cdFile.show();
            }
            else if((style & SWT.SAVE) == 0) // only warn about non-existing files when NOT saving a file
            {
                File file = new File(txt);
                fileExists = file.exists();
                if(fileExists)
                {
                    cdFile.hide();
                }
                else
                {
                    cdFile.setDescriptionText(I18N.get().getString(I18N.FIELDS_FILEFIELD_FILEDOESNOTEXIST));
                    cdFile.show();
                }
            }
            else
            {
                cdFile.hide();
            }
        }
        else
        {
            cdFile.hide();
        }
    }

    /**
     * Sets whether or not to show the text field's control decoration (standard is true).
     * One use case is when a filename for a to-be-created file must be entered.
     * 
     * @param isShowDecoration whether to show the control decoration or not
     */
    public void setShowDecoration(boolean isShowDecoration)
    {
        this.isShowDecoration = isShowDecoration;
        validate();
    }

    public boolean getShowDecoration()
    {
        return isShowDecoration;
    }

    /**
     * When set to true (standard is false), then after selecting a file via the "set"-button and when this file already
     * exists, the userwill be explicitely prompted whether the file shall be overwritten or not.
     * 
     * @param isOverwriteAsk whether to show a dialog for overwriting a file or not
     */
    public void setOverwriteAsk(boolean isOverwriteAsk)
    {
        this.isOverwriteAsk = isOverwriteAsk;
    }

    public boolean getOverwriteAsk()
    {
        return isOverwriteAsk;
    }

    public boolean getDoOverwrite()
    {
        return doOverwrite;
    }

    /**
     * May be used when style {@link SWT#MULTI} is used.
     * TODO multi-file selection support
     */
    @Override
    public String[] getValues()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the appropriate File object.
     * 
     * @return
     */
    public File getFile()
    {
        String value = getValue();
        if(value != null && value.length() > 0)
        {
            File file = new File(value);
            return file;
        }
        return null;
    }

    /**
     * May be used when style {@link SWT#MULTI} is used.
     * 
     * TODO multi-file selection support
     * @return
     */
    public File[] getFiles()
    {
        throw new UnsupportedOperationException();
    }
}
