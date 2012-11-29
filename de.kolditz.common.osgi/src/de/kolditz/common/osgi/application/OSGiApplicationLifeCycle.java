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
package de.kolditz.common.osgi.application;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.equinox.log.ExtendedLogService;
import org.osgi.service.event.Event;
import org.osgi.util.tracker.ServiceTracker;

import de.kolditz.common.osgi.EventHandlingPlugin;

/**
 * Synchronous Application Life Cycle. Call {@link #run()} to activate the lifecycle which may be controlled by remote
 * clients. Use {@link #getEventTopic()} to dynamically retrieve the actual event topic, which may be different each
 * time the application starts.
 * 
 * @author Till Kolditz - Till.Kolditz@gmail.com
 */
public abstract class OSGiApplicationLifeCycle implements IOSGiApplicationLifeCycle
{
    public static final Integer EXIT_FORCE = Integer.valueOf(1024);

    protected IApplication application;
    protected IApplicationContext appContext;
    protected Integer returnCode = null;
    protected EventHandlingPlugin eventHandler;

    protected ServiceTracker<ExtendedLogService, ExtendedLogService> extendedLogTracker = null;

    /**
     * Creates a new instance of this class. Use {@link #start()} in order to start the life cycle.
     * 
     * @param appContext
     *            the {@link IApplicationContext}
     */
    public OSGiApplicationLifeCycle(IApplication application, IApplicationContext appContext,
            EventHandlingPlugin eventHandler)
    {
        this.application = application;
        this.appContext = appContext;
        this.eventHandler = eventHandler;
    }

    @Override
    public void close()
    {
        synchronized (this)
        {
            sendEvent(EVENT_CLOSE);
            returnCode = IApplication.EXIT_OK;
            notifyAll();
        }
    }

    @Override
    public void restart()
    {
        synchronized (this)
        {
            sendEvent(EVENT_RESTART);
            returnCode = IApplication.EXIT_RESTART;
            notifyAll();
        }
    }

    @Override
    public void forceClose()
    {
        synchronized (this)
        {
            sendEvent(EVENT_CLOSE_FORCED);
            returnCode = EXIT_FORCE;
            notifyAll();
        }
    }

    @Override
    public void relaunch(Map<String, String> relaunchProperties)
    {
        synchronized (this)
        {
            sendEvent(EVENT_RELAUNCH);
            System.setProperty(IApplicationContext.EXIT_DATA_PROPERTY, foldMap(relaunchProperties));
            returnCode = IApplication.EXIT_RELAUNCH;
            notifyAll();
        }
    }

    private String foldMap(Map<String, String> map)
    {
        StringBuilder sb = new StringBuilder();
        for (Entry<String, String> e : map.entrySet())
        {
            sb.append(e.getKey());
            if (e.getValue() != null)
            {
                sb.append('=').append(e.getValue());
            }
            sb.append(' ');
        }
        return sb.toString();
    }

    /**
     * Synchronous
     * 
     * @param property
     *            the property to send using the {@link IOSGiApplicationLifeCycle#EVENT_PROPERTY} key
     */
    protected void sendEvent(String property)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(EVENT_PROPERTY, property);
        Event event = new Event(getEventTopic(), map);
        eventHandler.sendEvent(event);
    }

    /**
     * Asynchronous
     * 
     * @param property
     *            the property to send using the {@link IOSGiApplicationLifeCycle#EVENT_PROPERTY} key
     */
    protected void postEvent(String property)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(EVENT_PROPERTY, property);
        Event event = new Event(getEventTopic(), map);
        eventHandler.postEvent(event);
    }

    /**
     * Runs this OSGi application's actual life cycle. This call is blocking, waiting on this object.
     */
    public synchronized Integer run()
    {
        extendedLogTracker = new ServiceTracker<ExtendedLogService, ExtendedLogService>(
                eventHandler.getBundleContext(), ExtendedLogService.class, null);
        extendedLogTracker.open();
        while (returnCode == null)
        {
            try
            {
                wait();
            }
            catch (Exception e)
            {
                ExtendedLogService logService = (ExtendedLogService) extendedLogTracker.getService();
                if (logService != null)
                    logService.log(IStatus.ERROR, "Error", e); //$NON-NLS-1$
            }
        }
        return returnCode;
    }

    @Override
    public void testEvent(String message)
    {
        ExtendedLogService logService = (ExtendedLogService) extendedLogTracker.getService();
        if (logService != null)
            logService.log(IStatus.INFO, message);
        sendEvent(message);
    }
}
