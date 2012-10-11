/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 * created on 29.08.2012 at 18:39:53
 * 
 *  Contributors:
 *      Till Kolditz
 *******************************************************************************/
package de.kolditz.common.rcp;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

/**
 * 
 * @author Till Kolditz - Till.Kolditz@gmail.com
 */
public abstract class EventHandlingUIPlugin extends AbstractUIPlugin
{
    private EventAdmin eventAdmin;
    private ServiceReference<EventAdmin> srEventAdmin;
    private Map<EventHandler, ServiceRegistration<EventHandler>> eventHandlers;

    @Override
    public void start(BundleContext context) throws Exception
    {
        super.start(context);

        srEventAdmin = context.getServiceReference(EventAdmin.class);
        if(srEventAdmin == null)
        {
            getLog().log(new Status(IStatus.ERROR, getPluginID(), "no event admin found")); //$NON-NLS-1$
        }
        else
        {
            eventAdmin = (EventAdmin)context.getService(srEventAdmin);
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {
        eventAdmin = null;
        if(eventHandlers != null)
        {
            for(Entry<EventHandler, ServiceRegistration<EventHandler>> e : eventHandlers.entrySet())
            {
                context.ungetService(e.getValue().getReference());
            }
            eventHandlers.clear();
            eventHandlers = null;
        }
        if(srEventAdmin != null)
        {
            context.ungetService(srEventAdmin);
            srEventAdmin = null;
        }
        super.stop(context);
    }

    public EventAdmin getEventAdmin()
    {
        return eventAdmin;
    }

    /**
     * Asynchronous event posting.
     * 
     * @param event
     *            the {@link Event}
     */
    public void postEvent(Event event)
    {
        if(eventAdmin != null)
        {
            eventAdmin.postEvent(event);
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
        if(eventAdmin != null)
        {
            eventAdmin.sendEvent(event);
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
    public synchronized boolean registerEventHandler(EventHandler handler, Dictionary<String, ?> properties)
    {
        if(handler == null) return false;
        if(eventHandlers == null)
        {
            eventHandlers = new HashMap<EventHandler, ServiceRegistration<EventHandler>>();
        }
        try
        {
            eventHandlers.put(handler,
                    getBundle().getBundleContext().registerService(EventHandler.class, handler, properties));
            return true;
        }
        catch(Exception e)
        {
            getLog().log(
                    new Status(IStatus.ERROR, getPluginID(), "Could not register EventHandler " + handler.toString()));
            return false;
        }
    }

    public synchronized void unregisterEventHandler(EventHandler handler)
    {
        if(eventHandlers != null)
        {
            ServiceRegistration<EventHandler> sr = eventHandlers.get(handler);
            if(sr != null)
            {
                eventHandlers.remove(handler);
                getBundle().getBundleContext().ungetService(sr.getReference());
            }
        }
    }
}
