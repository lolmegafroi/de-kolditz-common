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

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.DirectoryDialog;

import de.kolditz.common.util.SystemProperties;

/**
 * @author Till Kolditz - Till.Kolditz@gmail.com
 */
public class FolderField extends FileField
{
    /**
     * @param parent
     * @param style
     * @param label
     * @param null_hint
     */
    public FolderField(FieldComposite parent, int style, String label)
    {
        this(parent, style, label, "");
    }

    /**
     * @param parent
     * @param style
     * @param label
     * @param null_hint
     */
    public FolderField(FieldComposite parent, int style, String label, String null_hint)
    {
        super(parent, style, label, null_hint);
    }

    /**
     * Not used for {@link FolderField}
     */
    @Override
    public void setFilter(String[] extensions, String[] names, int index)
    {
    }

    @Override
    protected void widgetSelected(SelectionEvent e)
    {
        if (e.widget == btnSet)
        {
            DirectoryDialog dd = new DirectoryDialog(text.getShell());
            String str = text.getText();
            dd.setMessage(null_hint);
            dd.setFilterPath(str.equals(null_hint) ? SystemProperties.USER_DIR : str);
            String target = dd.open();
            if (target != null)
            {
                text.setText(target);
                notifyObservers(target);
            }
        }
        else
        {
            super.widgetSelected(e);
        }
    }
}
