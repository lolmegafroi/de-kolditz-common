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
package de.kolditz.common.ui;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import de.kolditz.common.concurrent.Scheduler;
import de.kolditz.common.ui.i18n.I18N;
import de.kolditz.common.util.RegExpPatterns;

/**
 * Full-fledged text field for entering an IP V4 including a control decoration.
 * 
 * @author Till Kolditz - Till.Kolditz@gmail.com
 * @see InetAddress
 */
public class IPText extends AbstractControl implements IFillViewAware, IValidationControl
{
    private static final int MODIFY_CHECK_DELAY = 500;                                    // in ms
    private static final TimeUnit MOFIFY_CHECK_TU = TimeUnit.MILLISECONDS;

    protected Text text;
    protected boolean fillView = false;
    protected ControlDecoration cd;
    protected ScheduledFuture<?> ipFuture = null;
    protected boolean ipValid = false;
    private ListenerList modifyListeners = new ListenerList(ListenerList.IDENTITY);

    private RunInUIThread ipModifyRunner = new RunInUIThread()
    {
        private Pattern pIPV4 = RegExpPatterns.ipV4();

        @Override
        protected void inUIThread()
        {
            BusyIndicator.showWhile(text.getDisplay(), new Runnable()
            {
                @Override
                public void run()
                {
                    if (text.isDisposed())
                        return;
                    ipValid = pIPV4.matcher(text.getText()).find();
                    if (!ipValid)
                    {
                        try
                        {
                            InetAddress.getByName(text.getText());
                            ipValid = true;
                        }
                        catch (UnknownHostException e1)
                        {
                        }
                    }
                    if (cd != null)
                    {
                        if (ipValid)
                            cd.hide();
                        else
                            cd.show();
                    }
                    ipFuture = null;
                    Event e = new Event();
                    e.widget = text;
                    e.display = text.getDisplay();
                    e.time = (int) System.currentTimeMillis();
                    ModifyEvent me = new ModifyEvent(e);
                    for (Object o : modifyListeners.getListeners())
                    {
                        ((ModifyListener) o).modifyText(me);
                    }
                }
            });
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
        text.addModifyListener(new ModifyListener()
        {
            @Override
            public void modifyText(ModifyEvent e)
            {
                if (isFillView())
                {
                    ipModifyRunner.run(null, false);
                }
                else
                    if ((ipFuture == null) || ipFuture.isCancelled() || ipFuture.isDone())
                    {
                        ipFuture = Scheduler.schedule(ipModifyRunner, MODIFY_CHECK_DELAY, MOFIFY_CHECK_TU);
                    }
                    else
                    {
                        if (ipFuture != null)
                            ipFuture.cancel(false);
                        ipFuture = Scheduler.schedule(ipModifyRunner, MODIFY_CHECK_DELAY, MOFIFY_CHECK_TU);
                    }
            }
        });
    }

    public String getIP()
    {
        return text.getText();
    }

    /**
     * @param ip
     *            a valid v4 IP
     * @throws IllegalArgumentException
     */
    public void setIP(String ip)
    {
        text.setText(ip);
        text.notifyListeners(SWT.Modify, null);
    }

    /**
     * May be called only once. Further calls have no effect.
     * 
     * @param position
     */
    public void createControlDecoration(int position)
    {
        if (cd != null)
        {
            cd = new ControlDecoration(text, position);
            adaptCD();
        }
    }

    /**
     * May be called only once. Further calls have no effect.
     * 
     * @param position
     * @param drawComposite
     */
    public void createControlDecoration(int position, Composite drawComposite)
    {
        if (cd != null)
        {
            cd = new ControlDecoration(text, position, drawComposite);
            adaptCD();
        }
    }

    private void adaptCD()
    {
        cd.setDescriptionText(I18N.get().getString(I18N.CONTROLS_IPTEXT_INVALID));
        cd.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR)
                .getImage());
        cd.setMarginWidth(2);
        cd.hide();
    }

    /**
     * Listeners are only notified after a certain amount of time (when the input was validated).
     * 
     * @param listener
     */
    public void addModifyListener(ModifyListener listener)
    {
        modifyListeners.add(listener);
    }

    public void removeModifyListener(ModifyListener listener)
    {
        modifyListeners.remove(listener);
    }

    @Override
    public void setFillView(boolean isFillView)
    {
        fillView = isFillView;
    }

    @Override
    public boolean isFillView()
    {
        return fillView;
    }

    @Override
    public boolean isValid()
    {
        return ipValid;
    }
}
