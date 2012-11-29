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

import de.kolditz.common.concurrent.MultiThreaded;
import de.kolditz.common.concurrent.Scheduler;

/**
 * An abstract utility class which enforces the protocol that first {@link #async()} is called in a separate thread, and
 * afterwards {@link #sync()} is called in the SWT UI thread. It returns a {@link Future} object so that clients may
 * even test for completion.
 * 
 * @author Till Kolditz - Till.Kolditz@gmail.com
 */
public abstract class ASyncWorker
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
            try
            {
                async();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            Display.getDefault().syncExec(new SyncRunnable());
        }
    }

    private Object transfer = null;
    private Future<?> future = null;

    public ASyncWorker()
    {
    }

    public ASyncWorker(Object transfer)
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
     * This method is run asynchronously from the SWT UI thread.
     */
    @MultiThreaded
    protected abstract void async();

    /**
     * This method is run in the SWT UI thread and does nothing. May be overwritten by clients.
     */
    protected void sync()
    {
    }

    /**
     * Starts the worker thread. This may happen multiple times. Clients must not call {@link Future#get() get()} or
     * {@link Future#get(long, TimeUnit) get(long, TimeUnit)} on the returned Future object from within the UI thread
     * since this will completely block the UI thread and make the whole application unresponsive.
     */
    public final synchronized Future<?> start()
    {
        return future = Scheduler.submit(new ASyncRunnable());
    }

    /**
     * Starts the worker thread. This may happen multiple times. Clients must not call {@link Future#get() get()} or
     * {@link Future#get(long, TimeUnit) get(long, TimeUnit)} on the returned Future object from within the UI thread
     * since this will completely block the UI thread and make the whole application unresponsive.
     */
    public final synchronized Future<?> start(long delay, TimeUnit timeUnit)
    {
        return future = Scheduler.schedule(new ASyncRunnable(), delay, timeUnit);
    }

    /**
     * @return true when this worker was not started yet or is really done, false otherwise.
     */
    public final synchronized boolean isDone()
    {
        return future != null && future.isDone();
    }

    /**
     * @return true when this worker was not started yet or is really canceled, false otherwise.
     */
    public final synchronized boolean isCanceled()
    {
        return future != null && future.isCancelled();
    }

    /**
     * @return true when this worker was not started yet or is really running, false otherwise.
     */
    public final synchronized boolean isRunning()
    {
        return future != null && future != null && !future.isDone();
    }
}
