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
package de.kolditz.common.ui;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import de.kolditz.common.util.Pair;

/**
 * A simple abstract {@link Runnable} that is used to set some application-specific value and then call its
 * {@link #run()} method in the SWT UI thread. Clients need not call the {@link #run()} method because the
 * {@link #setValue(Display, Object)} or {@link #setValue(Display, Object, boolean)} methods take care of running the
 * code in the UI thread.
 * <p>
 * This class is intended to be used for (highly) asynchronous or multi-threaded applications by giving a simple
 * abstract mechanism for running code appropriately in the {@link SWT} {@link Display}'s thread
 * <p>
 * Use the protected variable value
 * 
 * @see #value
 * @author Till Kolditz - Till.Kolditz@gmail.com
 */
public abstract class SetInUIThread<E> implements Runnable
{
    /**
     * Convenience class. Use a {@link Pair} the following way: first is the text, second is the tool tip.
     * 
     * @see Pair
     * @see Pair#first(Object)
     * @see Pair#second(Object)
     * @author Till Kolditz - Till.Kolditz@gmail.com
     */
    public static class SetText extends SetInUIThread<Pair<String, String>>
    {
        private Text text;

        public SetText(Text text)
        {
            this.text = text;
        }

        @Override
        public void run()
        {
            text.setText(value.first());
            text.setToolTipText(value.second());
        }
    }

    /**
     * Convenience class. Use a {@link Boolean} to set the selection on one of the following widgets:
     * <ul>
     * <li>{@link Button}</li>
     * </ul>
     * 
     * @author Till Kolditz - Till.Kolditz@gmail.com
     */
    public static class SetSelection extends SetInUIThread<Boolean>
    {
        private Button button;

        public SetSelection(Button button)
        {
            this.button = button;
        }

        @Override
        public void run()
        {
            button.setSelection(value);
        }
    }

    protected Logger log = Logger.getLogger(getClass().getSimpleName());
    protected E value;

    /**
     * Sets the value for this abstract runner. The run method will either be called directly when the current thread is
     * the UI thread or, otherwise, asynchronously in the UI thread.
     * 
     * @param display
     *            the {@link Display}
     * @param value
     *            the applciation-specific boolean value
     */
    public void setValue(Display display, E value)
    {
        setValue(display, value, true);
    }

    /**
     * Sets the value for this abstract runner. The run method will either be called directly when the current thread is
     * the UI thread or, otherwise, depending on async in the UI thread.
     * 
     * @param display
     *            The {@link Display}.
     * @param value
     *            The applciation-specific boolean value.
     * @param async
     *            If set to false this method will block until the UI thread has executed this runner's {@link #run()}
     *            method. Otherwise the {@link #run()} method will be called by the display's
     *            {@link Display#asyncExec(Runnable)} method.
     */
    public void setValue(Display display, E value, boolean async)
    {
        if (display == null || display.isDisposed())
        {
            return;
        }
        this.value = value;
        if (display.getThread() == Thread.currentThread())
        {
            log.trace("running in UI thread");
            run();
        }
        else
        {
            log.trace("running async");
            display.asyncExec(this);
        }
    }
}
