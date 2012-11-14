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
import java.net.UnknownHostException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import de.kolditz.common.ui.IValidationListener;
import de.kolditz.common.ui.concurrent.ASyncWorker;
import de.kolditz.common.util.RegExpPatterns;

/**
 * Full-fledged text field for entering an IP V4 including a control decoration.
 * 
 * @author Till Kolditz - Till.Kolditz@gmail.com
 * @see InetAddress
 */
public class IPText extends AbstractValidationControl
{
    private static final int MODIFY_CHECK_DELAY = 500; // in ms
    private static final TimeUnit MOFIFY_CHECK_TU = TimeUnit.MILLISECONDS;

    protected static final Pattern pIPV4 = RegExpPatterns.ipV4();

    private class Validator extends ASyncWorker
    {
        private boolean doNotify;
        private String txt;
        private boolean valid;

        Validator(boolean doNotify, String txt)
        {
            this.doNotify = doNotify;
            this.txt = txt;
        }

        @Override
        protected void async()
        {
            valid = pIPV4.matcher(txt).find();
            if (valid == false)
            {
                try
                {
                    InetAddress.getByName(txt);
                    valid = true;
                }
                catch (UnknownHostException e)
                {
                }
            }
            setValid(valid);
        }

        @Override
        protected void sync()
        {
            if (cd != null)
            {
                if (valid)
                    cd.hide();
                else
                    cd.show();
            }
            if (doNotify)
                notifyValidationListeners();
            ipFuture = null;
        }
    }

    protected Text text;
    protected Future<?> ipFuture = null;
    protected boolean doNotifyListeners = true;
    protected ModifyListener internalModifyListener = new ModifyListener()
    {
        @Override
        public void modifyText(ModifyEvent e)
        {
            if (ipFuture != null && !ipFuture.isDone())
                ipFuture.cancel(true);
            ipFuture = new Validator(doNotifyListeners, text.getText()).start(MODIFY_CHECK_DELAY, MOFIFY_CHECK_TU);
        }
    };

    public IPText(Composite parent, int style)
    {
        text = new Text(parent, style);
        init();
    }

    public IPText(Composite parent, int style, FormToolkit toolkit)
    {
        text = toolkit.createText(parent, "", style); //$NON-NLS-1$
        init();
    }

    private void init()
    {
        setControl(text);
        text.addModifyListener(internalModifyListener);
    }

    public String getIP()
    {
        return text.getText();
    }

    /**
     * @param ip
     *            a valid v4 IP
     * @param doNotifyListeners
     *            whether or not to notify the {@link IValidationListener}s
     * @throws IllegalArgumentException
     */
    public void setIP(String ip, boolean doNotifyListeners)
    {
        this.doNotifyListeners = doNotifyListeners;
        text.setText(ip);
    }
}
