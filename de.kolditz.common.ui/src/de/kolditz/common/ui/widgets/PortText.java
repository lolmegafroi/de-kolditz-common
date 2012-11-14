/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 * created on 21.08.2012 at 17:43:11
 * 
 *  Contributors:
 *      Till Kolditz
 *******************************************************************************/
package de.kolditz.common.ui.widgets;

import java.net.InetAddress;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import de.kolditz.common.ui.IValidationListener;

/**
 * Text field for entering a port/socket including a control decoration.
 * 
 * @author Till Kolditz - Till.Kolditz@gmail.com
 * @see InetAddress
 */
public class PortText extends AbstractValidationControl
{
    protected Text text;
    protected boolean doNotifyListeners = true;
    protected ModifyListener internalModifyListener = new ModifyListener()
    {
        @Override
        public void modifyText(ModifyEvent e)
        {
            setValid(false);
            if (text.isDisposed())
                return;
            try
            {
                int port = Integer.parseInt(text.getText());
                setValid(port > 0 && port < 65536);
            }
            catch (NumberFormatException ex)
            {
            }
            if (cd != null)
            {
                if (isValid())
                    cd.hide();
                else
                    cd.show();
            }
            if (doNotifyListeners)
                notifyValidationListeners();
        }
    };

    public PortText(Composite parent, int style)
    {
        text = new Text(parent, style);
        init();
    }

    public PortText(Composite parent, int style, FormToolkit toolkit)
    {
        text = toolkit.createText(parent, "", style); //$NON-NLS-1$
        init();
    }

    private void init()
    {
        setControl(text);
        text.setTextLimit(5);
        text.addModifyListener(internalModifyListener);
    }

    public String getText()
    {
        return text.getText();
    }

    public Integer getPort()
    {
        return Integer.valueOf(getText());
    }

    /**
     * @param port
     *            a valid port
     * @param doNotifyListeners
     *            whether or not to notify the {@link IValidationListener}s
     * @throws IllegalArgumentException
     */
    public void setPort(String port, boolean doNotifyListeners)
    {
        this.doNotifyListeners = doNotifyListeners;
        text.setText(port);
    }

    /**
     * @param port
     *            a valid port
     * @param doNotifyListeners
     *            whether or not to notify modifyListeners
     */
    public void setPort(int port, boolean doNotifyListeners)
    {
        setPort(Integer.toString(port), doNotifyListeners);
    }
}
