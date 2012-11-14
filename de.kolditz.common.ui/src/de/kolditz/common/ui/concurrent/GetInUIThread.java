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
package de.kolditz.common.ui.concurrent;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

/**
 * A simple abstract {@link Runnable} that is used to retrieve some widget(s)'s value(s) from inside the SWT UI thread.
 * Clients need not call the {@link #run()} method because the {@link #get(Display)} method takes care of running the
 * code in the UI thread.
 * <p>
 * This class is intended to be used for (highly) asynchronous or multi-threaded applications by giving a simple
 * abstract mechanism for running code appropriately in the {@link SWT} {@link Display}'s thread
 * 
 * @author Till Kolditz - Till.Kolditz@gmail.com
 */
public abstract class GetInUIThread<E> implements Runnable
{
    /**
     * Convenience class for one of the following widgets:
     * <ul>
     * <li>{@link Text}</li>
     * </ul>
     * 
     * @see GetInUIThread
     * @author Till Kolditz - Till.Kolditz@gmail.com
     */
    public static class GetText extends GetInUIThread<String>
    {
        private Text text;

        public GetText(Text text)
        {
            this.text = text;
        }

        @Override
        public void run()
        {
            value = text.getText();
        }
    }

    /**
     * Convenience class for one of the following widgets:
     * <ul>
     * <li>{@link Button}</li>
     * </ul>
     * 
     * @see GetInUIThread
     * @author Till Kolditz - Till.Kolditz@gmail.com
     */
    public static class GetSelection extends GetInUIThread<Boolean>
    {
        private Button button;

        public GetSelection(Button button)
        {
            this.button = button;
        }

        @Override
        public void run()
        {
            value = Boolean.valueOf(button.getSelection());
        }
    }

    protected Logger log = Logger.getLogger(getClass().getSimpleName());
    protected E value;

    /**
     * Retrieves the value for this abstract runner. This call is blocking.
     * 
     * @param display
     *            The {@link Display}.
     */
    public E get(Display display)
    {
        Display d = display;
        if (d == null)
        {
            log.trace("display is null");
            d = Display.getDefault();
        }
        if (d.getThread() == Thread.currentThread())
        {
            log.trace("running in UI thread");
            run();
        }
        else
        {
            log.trace("running sync");
            d.syncExec(this);
        }
        return value;
    }
}
