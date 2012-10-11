/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 * created on 29.08.2012 at 20:19:52
 * 
 *  Contributors:
 *      Till Kolditz
 *******************************************************************************/
package de.kolditz.common.ui.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.kolditz.common.util.IValidator;

/**
 * A simple dialog for entering some String property
 * 
 * @author Till Kolditz - Till.Kolditz@gmail.com
 */
public class EnterPropertyDialog extends Dialog
{
    private ControlDecoration cdKey, cdValue;
    private Text tfKey, tfValue;
    private IValidator<String> keyValidator, valueValidator;
    private String key = null, value = null;

    /**
     * @param parentShell
     */
    public EnterPropertyDialog(Shell parentShell)
    {
        super(parentShell);
    }

    /**
     * @param parentShell
     */
    public EnterPropertyDialog(IShellProvider parentShell)
    {
        super(parentShell);
    }

    @Override
    protected Control createDialogArea(Composite parent)
    {
        Composite dialogArea = (Composite)super.createDialogArea(parent);

        new Label(dialogArea, SWT.NONE).setText("Key:"); // TODO i18n
        tfKey = new Text(parent, SWT.SINGLE | SWT.BORDER);
        if(key != null)
        {
            tfKey.setText(key);
        }
        if(keyValidator != null)
        {
            cdKey = new ControlDecoration(tfKey, SWT.TOP | SWT.LEFT, dialogArea);
            cdKey.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR)
                    .getImage());
            cdKey.hide();
            tfKey.addModifyListener(new ModifyListener()
            {
                @Override
                public void modifyText(ModifyEvent e)
                {
                    String str = keyValidator.validate(tfKey.getText());
                    if(str != null)
                    {
                        cdKey.setDescriptionText(str);
                        cdKey.show();
                    }
                    else
                    {
                        cdKey.hide();
                    }
                    key = str;
                }
            });
        }

        new Label(dialogArea, SWT.NONE).setText("Value:"); // TODO i18n
        tfValue = new Text(dialogArea, SWT.SINGLE | SWT.BORDER);
        if(value != null)
        {
            tfValue.setText(value);
        }
        if(valueValidator != null)
        {
            cdValue = new ControlDecoration(tfKey, SWT.TOP | SWT.LEFT, dialogArea);
            cdValue.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR)
                    .getImage());
            cdValue.hide();
            tfValue.addModifyListener(new ModifyListener()
            {
                @Override
                public void modifyText(ModifyEvent e)
                {
                    String str = valueValidator.validate(tfValue.getText());
                    if(str != null)
                    {
                        cdValue.setDescriptionText(str);
                        cdValue.show();
                    }
                    else
                    {
                        cdValue.hide();
                    }
                    value = str;
                }
            });
        }

        return dialogArea;
    }

    public void setKeyValidator(IValidator<String> keyValidator)
    {
        this.keyValidator = keyValidator;
    }

    public void setValueValidator(IValidator<String> valueValidator)
    {
        this.valueValidator = valueValidator;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public void setValue(String value)
    {
        this.value = value;
    }
}
