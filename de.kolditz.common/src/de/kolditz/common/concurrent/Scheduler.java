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

    private static final Object lock;
    private static volatile ScheduledThreadPoolExecutor executor;

    static
    {
        lock = new Object();
        checkState();
    }

    private static void checkState()
    {
        boolean recreate = false;
        if (executor != null)
        {
            if (executor.isShutdown() || executor.isTerminating() || executor.isTerminated())
            {
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
                    // fix to allow termination when neither shutdown() nor shutdownNow() were called
                    executor = new ScheduledThreadPoolExecutor(0);
                    executor.setKeepAliveTime(10, TimeUnit.SECONDS);
                    executor.allowCoreThreadTimeOut(true);
                }
            }
        }
    }

    public static void shutdown()
    {
        executor.shutdown();
    }

    public static List<Runnable> shutdownNow()
    {
        List<Runnable> notShutdown = executor.shutdownNow();
        executor = null;
        return notShutdown;
    }

    public static void purge()
    {
        checkState();
        executor.purge();
    }

    public static boolean isShutdown()
    {
        checkState();
        return executor.isShutdown();
    }

    public static boolean isTerminated()
    {
        checkState();
        return executor.isTerminated();
    }

    public static boolean isTerminating()
    {
        checkState();
        return executor.isTerminating();
    }

    public static boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException
    {
        checkState();
        return executor.awaitTermination(timeout, unit);
    }

    public static <T> Future<T> submit(Callable<T> task)
    {
        checkState();
        return executor.submit(task);
    }

    public static <T> Future<T> submit(Runnable task, T result)
    {
        checkState();
        return executor.submit(task, result);
    }

    public static Future<?> submit(Runnable task)
    {
        checkState();
        return executor.submit(task);
    }

    public static <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException
    {
        checkState();
        return executor.invokeAll(tasks);
    }

    public static <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException
    {
        checkState();
        return executor.invokeAll(tasks, timeout, unit);
    }

    public static <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException,
            ExecutionException
    {
        checkState();
        return executor.invokeAny(tasks);
    }

    public static <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException
    {
        checkState();
        return executor.invokeAny(tasks, timeout, unit);
    }

    public static void execute(Runnable command)
    {
        checkState();
        executor.execute(command);
    }

    public static ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit)
    {
        checkState();
        return executor.schedule(command, delay, unit);
    }

    public static <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit)
    {
        checkState();
        return executor.schedule(callable, delay, unit);
    }

    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit)
    {
        checkState();
        return executor.scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay,
            TimeUnit unit)
    {
        checkState();
        return executor.scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }
}
