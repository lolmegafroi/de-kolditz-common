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
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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

/**
 * Full-fledged text field for entering an IP V4 including a control decoration.
 * 
 * @author Till Kolditz - Till.Kolditz@gmail.com
 * @see InetAddress
 */
public class PortText extends AbstractControl implements IFillViewAware, IValidationControl
{
    private static final int MODIFY_CHECK_DELAY = 500;                                    // in ms
    private static final TimeUnit MOFIFY_CHECK_TU = TimeUnit.MILLISECONDS;

    protected Text text;
    protected boolean fillView = false;
    protected ControlDecoration cd;
    protected ScheduledFuture<?> portFuture = null;
    protected boolean portValid = false;
    private ListenerList modifyListeners = new ListenerList(ListenerList.IDENTITY);

    private RunInUIThread ipModifyRunner = new RunInUIThread()
    {
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
                    try
                    {
                        int port = Integer.parseInt(text.getText());
                        portValid = port > 0 && port < 65536;
                    }
                    catch (NumberFormatException e)
                    {
                        portValid = false;
                    }
                    if (cd != null)
                    {
                        if (portValid)
                            cd.hide();
                        else
                            cd.show();
                    }
                    portFuture = null;
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
                    if ((portFuture == null) || portFuture.isCancelled() || portFuture.isDone())
                    {
                        portFuture = Scheduler.schedule(ipModifyRunner, MODIFY_CHECK_DELAY, MOFIFY_CHECK_TU);
                    }
                    else
                    {
                        if (portFuture != null)
                            portFuture.cancel(false);
                        portFuture = Scheduler.schedule(ipModifyRunner, MODIFY_CHECK_DELAY, MOFIFY_CHECK_TU);
                    }
            }
        });
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
     * @throws IllegalArgumentException
     */
    public void setPort(String port)
    {
        text.setText(port);
        text.notifyListeners(SWT.Modify, null);
    }

    public void setPort(int port)
    {
        setPort(Integer.toString(port));
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
        cd.setDescriptionText(I18N.get().getString(I18N.CONTROLS_PORTTEXT_INVALID));
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
        return portValid;
    }
}
