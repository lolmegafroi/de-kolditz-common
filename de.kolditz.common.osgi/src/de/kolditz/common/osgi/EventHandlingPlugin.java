/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 * created on 29.08.2012 at 17:55:10
 * 
 *  Contributors:
 *      Till Kolditz
 *******************************************************************************/
package de.kolditz.common.osgi;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

/**
 * @author Till Kolditz - Till.Kolditz@gmail.com
 */
public abstract class EventHandlingPlugin extends Plugin
{
    private BundleContext bundleContext;
    private EventAdmin eventAdmin;
    private ServiceReference<EventAdmin> srEventAdmin;
    private Map<EventHandler, ServiceRegistration<EventHandler>> eventHandlers;

    @Override
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        bundleContext = context;

        srEventAdmin = context.getServiceReference(EventAdmin.class);
        if (srEventAdmin == null)
        {
            getLog().log(new Status(IStatus.ERROR, getPluginID(), "no event admin found")); //$NON-NLS-1$
        }
        else
        {
            eventAdmin = context.getService(srEventAdmin);
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {
        if (eventHandlers != null)
        {
            for (Entry<EventHandler, ServiceRegistration<EventHandler>> e : eventHandlers.entrySet())
            {
                e.getValue().unregister();
            }
            eventHandlers.clear();
        }
        if (srEventAdmin != null)
        {
            context.ungetService(srEventAdmin);
        }
        bundleContext = null;
        eventAdmin = null;
        srEventAdmin = null;
        super.stop(context);
    }

    public BundleContext getBundleContext()
    {
        return bundleContext;
    }

    public EventAdmin getEventAdmin()
    {
        return eventAdmin;
    }

    private Event prepareEvent(String topic, String[] keys, Object[] values)
    {
        Map<String, Object> properties = new HashMap<String, Object>();
        if ((keys == null) != (values == null))
            throw new IllegalArgumentException("keys=" + (keys == null ? "null" : "not null") + " but values="
                    + (values == null ? "null" : "not null"));
        if (keys != null)
        {
            if (keys.length != values.length)
                throw new IllegalArgumentException("keys.length=" + keys.length + " but values.lenght=" + values.length);
            for (int i = 0; i < keys.length; ++i)
                properties.put(keys[i], values[i]);
        }
        return new Event(topic, properties);
    }

    /**
     * Asynchronous event posting.
     * 
     * @param event
     *            the {@link Event}
     */
    public void postEvent(Event event)
    {
        if (eventAdmin != null)
        {
            eventAdmin.postEvent(event);
        }
    }

    /**
     * Asynchronous event posting. <code>keys</code> and <code>values</code> must both be null or not null and have the
     * same length.
     * 
     * @param topic
     *            the event's topic
     * @param keys
     *            the topic properties' keys
     * @param values
     *            the topic properties' values
     */
    public void postEvent(String topic, String[] keys, Object[] values)
    {
        if (eventAdmin != null)
        {
            eventAdmin.postEvent(prepareEvent(topic, keys, values));
        }
    }

    /**
     * Synchronous event sending.
     * 
     * @param event
     *            the {@link Event}
     */
    public void sendEvent(Event event)
    {
        if (eventAdmin != null)
        {
            eventAdmin.sendEvent(event);
        }
    }

    /**
     * Synchronous event posting. <code>keys</code> and <code>values</code> must both be null or not null and have the
     * same length.
     * 
     * @param topic
     *            the event's topic
     * @param keys
     *            the topic properties' keys
     * @param values
     *            the topic properties' values
     */
    public void sendEvent(String topic, String[] keys, Object[] values)
    {
        if (eventAdmin != null)
        {
            eventAdmin.sendEvent(prepareEvent(topic, keys, values));
        }
    }

    protected abstract String getPluginID();

    /**
     * Registers an {@link EventHandler} with the given properties.
     * 
     * @param handler
     *            the EventHandler
     * @param properties
     *            the Dictionary
     * @return whether registration was successful or not (e.g. when handler is null or some exception was caught)
     * @see EventConstants#EVENT_TOPIC
     * @see EventConstants#EVENT_FILTER
     * @see EventConstants#EVENT_DELIVERY
     */
    public boolean registerEventHandler(EventHandler handler, Dictionary<String, ?> properties)
    {
        if (handler == null)
            return false;
        if (eventHandlers == null)
        {
            eventHandlers = new HashMap<EventHandler, ServiceRegistration<EventHandler>>();
        }
        try
        {
            eventHandlers.put(handler,
                    getBundle().getBundleContext().registerService(EventHandler.class, handler, properties));
            return true;
        }
        catch (Exception e)
        {
            getLog().log(
                    new Status(IStatus.ERROR, getPluginID(), "Could not register EventHandler " + handler.toString()));
            return false;
        }
    }

    public void unregisterEventHandler(EventHandler handler)
    {
        if (handler != null && eventHandlers != null)
        {
            ServiceRegistration<EventHandler> sreg = eventHandlers.remove(handler);
            if (sreg != null)
            {
                sreg.unregister();
            }
        }
    }
}
