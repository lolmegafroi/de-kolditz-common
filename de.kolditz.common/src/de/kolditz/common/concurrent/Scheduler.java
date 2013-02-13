/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Till Kolditz
 *******************************************************************************/
package de.kolditz.common.concurrent;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A scheduler utility class which internally uses a single {@link ScheduledThreadPoolExecutor} instance. It allows to
 * be shutdown and will recreate the executor when necessary. This utility class is intended to be used without ANY
 * configuration, so if you really need to configure an executor, create one on your own.
 * <p>
 * This executor sets a thread idle time of 10 (ten) seconds and uses no core threads in order to allow graceful
 * termination of runtimes without the need to explicitely call {@link #shutdown()} or {@link #shutdownNow()}.
 * </p>
 * 
 * @author Till Kolditz - Till.Kolditz@gmail.com
 */
public final class Scheduler
{
    private Scheduler()
    {
    }

    private static final Object lock = new Object();
    private static volatile ScheduledThreadPoolExecutor executor;

    static
    {
        checkState();
    }

    private static void checkState()
    {
        boolean recreate = false;
        if (executor != null)
        {
            if (executor.isShutdown() || executor.isTerminating() || executor.isTerminated())
            {
                executor = null;
                recreate = true;
            }
        }
        else
        {
            recreate = true;
        }
        if (recreate)
        {
            synchronized (lock)
            {
                if (executor == null)
                {
                    // (hopefully) fix to allow termination when neither shutdown() nor shutdownNow() were called
                    executor = new ScheduledThreadPoolExecutor(16); // Maximum of 16 threads
                    executor.setKeepAliveTime(30, TimeUnit.SECONDS);
                    executor.allowCoreThreadTimeOut(true);
                }
            }
        }
    }

    /**
     * Initiates an orderly shutdown in which previously submitted tasks are executed. Synchronized.
     * 
     * @see ScheduledThreadPoolExecutor#shutdown()
     */
    public static void shutdown()
    {
        synchronized (lock)
        {
            if (executor != null)
                executor.shutdown();
            executor = null;
        }
    }

    /**
     * Attempts to stop all actively executing tasks, halts the processing of waiting tasks, and returns a list of the
     * tasks that were awaiting execution.
     * 
     * @return list of tasks that never commenced execution. <b>May be empty or null</b>
     * @see ScheduledThreadPoolExecutor#shutdownNow()
     */
    public static List<Runnable> shutdownNow()
    {
        List<Runnable> notShutdown = null;
        synchronized (lock)
        {
            if (executor != null)
            {
                notShutdown = executor.shutdownNow();
                executor = null;
            }
        }
        return notShutdown;
    }

    /**
     * @see ScheduledThreadPoolExecutor#purge()
     */
    public static void purge()
    {
        checkState();
        executor.purge();
    }

    /**
     * @see ScheduledThreadPoolExecutor#isShutdown()
     */
    public static boolean isShutdown()
    {
        checkState();
        return executor.isShutdown();
    }

    /**
     * @see ScheduledThreadPoolExecutor#isTerminated()
     */
    public static boolean isTerminated()
    {
        checkState();
        return executor.isTerminated();
    }

    /**
     * @see ScheduledThreadPoolExecutor#isTerminating()
     */
    public static boolean isTerminating()
    {
        checkState();
        return executor.isTerminating();
    }

    /**
     * @see ScheduledThreadPoolExecutor#awaitTermination(long, TimeUnit)
     */
    public static boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException
    {
        checkState();
        return executor.awaitTermination(timeout, unit);
    }

    /**
     * @see ScheduledThreadPoolExecutor#submit(Callable)
     */
    public static <T> Future<T> submit(Callable<T> task)
    {
        checkState();
        return executor.submit(task);
    }

    /**
     * @see ScheduledThreadPoolExecutor#submit(Runnable, Object)
     */
    public static <T> Future<T> submit(Runnable task, T result)
    {
        checkState();
        return executor.submit(task, result);
    }

    /**
     * @see ScheduledThreadPoolExecutor#submit(Runnable)
     */
    public static Future<?> submit(Runnable task)
    {
        checkState();
        return executor.submit(task);
    }

    /**
     * @see ScheduledThreadPoolExecutor#invokeAll(Collection)
     */
    public static <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException
    {
        checkState();
        return executor.invokeAll(tasks);
    }

    /**
     * @see ScheduledThreadPoolExecutor#invokeAll(Collection, long, TimeUnit)
     */
    public static <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException
    {
        checkState();
        return executor.invokeAll(tasks, timeout, unit);
    }

    /**
     * @see ScheduledThreadPoolExecutor#invokeAny(Collection)
     */
    public static <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException,
            ExecutionException
    {
        checkState();
        return executor.invokeAny(tasks);
    }

    /**
     * @see ScheduledThreadPoolExecutor#invokeAny(Collection, long, TimeUnit)
     */
    public static <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException
    {
        checkState();
        return executor.invokeAny(tasks, timeout, unit);
    }

    /**
     * @see ScheduledThreadPoolExecutor#execute(Runnable)
     */
    public static void execute(Runnable command)
    {
        checkState();
        executor.execute(command);
    }

    /**
     * @see ScheduledThreadPoolExecutor#schedule(Runnable, long, TimeUnit)
     */
    public static ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit)
    {
        checkState();
        return executor.schedule(command, delay, unit);
    }

    /**
     * @see ScheduledThreadPoolExecutor#schedule(Runnable, long, TimeUnit)
     */
    public static <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit)
    {
        checkState();
        return executor.schedule(callable, delay, unit);
    }

    /**
     * @see ScheduledThreadPoolExecutor#scheduleAtFixedRate(Runnable, long, long, TimeUnit)
     */
    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit)
    {
        checkState();
        return executor.scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    /**
     * @see ScheduledThreadPoolExecutor#scheduleWithFixedDelay(Runnable, long, long, TimeUnit)
     */
    public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay,
            TimeUnit unit)
    {
        checkState();
        return executor.scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }
}
