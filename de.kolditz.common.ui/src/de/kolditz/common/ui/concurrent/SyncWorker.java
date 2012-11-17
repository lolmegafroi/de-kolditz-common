/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 * created on 14.11.2012 at 19:02:20
 * 
 *  Contributors:
 *      Till Kolditz
 *******************************************************************************/
package de.kolditz.common.ui.concurrent;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.widgets.Display;

import de.kolditz.common.concurrent.Scheduler;

/**
 * An abstract utility class whose {@link #sync()} method will be called in the UI thread. Unlike {@link ASyncWorker},
 * the start methods will not return a Future object, and there are 2 start methods:
 * <ul>
 * <li>{@link #start(boolean)} with <code>false</code> allows to block the calling thread when necessary</li>
 * <li>{@link #start(long, TimeUnit)} will always call sync asynchronously</li>
 * </ul>
 * 
 * @author Till Kolditz - Till.Kolditz@gmail.com
 */
public abstract class SyncWorker
{
    private class SyncRunnable implements Runnable
    {
        @Override
        public void run()
        {
            sync();
        }
    }

    private class ASyncRunnable implements Runnable
    {
        @Override
        public void run()
        {
            Display.getDefault().syncExec(new SyncRunnable());
        }
    }

    private Object transfer = null;
    private Future<?> future = null;
    private boolean sync = false;

    /**
     * Creates a new SyncWorker which starts an asynchronous thread e.g. for better UI responsiveness.
     */
    public SyncWorker()
    {
    }

    public SyncWorker(Object transfer)
    {
        this.transfer = transfer;
    }

    public synchronized Object getTransfer()
    {
        return transfer;
    }

    public synchronized void setTransfer(Object transfer)
    {
        this.transfer = transfer;
    }

    /**
     * This method is run in the SWT UI thread.
     */
    protected abstract void sync();

    /**
     * Starts the worker thread. This may happen multiple times.
     * 
     * @param async
     *            whether the {@link Display}'s {@link Display#asyncExec(Runnable) asyncExec} method shall be used when
     *            this code runs inside the UI thread
     */
    public final synchronized void start(boolean async)
    {
        future = null;
        if (async)
        {
            sync = false;
            future = Scheduler.submit(new ASyncRunnable());
        }
        else
        {
            sync = true;
            Display d = Display.getCurrent();
            if (d != null) // we ARE in the UI thread
            {
                sync();
            }
            else
            {
                Display.getDefault().syncExec(new SyncRunnable());
            }
        }
    }

    /**
     * Starts the worker thread. This may happen multiple times. {@link #sync()} will be executed in the UI thread after
     * the given delay.
     */
    public final synchronized void start(long delay, TimeUnit timeUnit)
    {
        sync = false;
        future = Scheduler.schedule(new ASyncRunnable(), delay, timeUnit);
    }

    /**
     * @return whether this worker was started and is really done
     */
    public final synchronized boolean isDone()
    {
        return sync || (future != null && future.isDone());
    }

    /**
     * @return whether this worker was started and is really canceled
     */
    public final synchronized boolean isCanceled()
    {
        return (!sync) && future != null && future.isCancelled();
    }

    /**
     * @return whether this worker was started and is really running
     */
    public final synchronized boolean isRunning()
    {
        return (!sync) && future != null && future != null && !future.isDone();
    }
}
