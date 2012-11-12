/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 * created on 26.09.2012 at 18:16:22
 * 
 *  Contributors:
 *      Till Kolditz
 *******************************************************************************/
package de.kolditz.common.osgi;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Sends {@link LogEntry}s into the OSGi event bus through an {@link EventAdmin}.
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public class EventForwardingLogListener extends LogEventForwarder implements LogListener
{
    private class LogServiceListener implements ServiceListener
    {
        public void serviceChanged(ServiceEvent event)
        {
            BundleContext bc = event.getServiceReference().getBundle().getBundleContext();
            // since we use a filter, this cast should be safe
            LogReaderService lrs = (LogReaderService) bc.getService(event.getServiceReference());
            if (lrs != null)
            {
                if (event.getType() == ServiceEvent.REGISTERED)
                {
                    m_readers.add(lrs);
                    lrs.addLogListener(EventForwardingLogListener.this);
                }
                else if (event.getType() == ServiceEvent.UNREGISTERING)
                {
                    lrs.removeLogListener(EventForwardingLogListener.this);
                    m_readers.remove(lrs);
                }
            }
        }
    }

    private BundleContext context;

    private LinkedList<LogReaderService> m_readers = new LinkedList<LogReaderService>();

    /**
     * We use a ServiceListener to dynamically keep track of all the LogReaderService service being registered or
     * unregistered
     * <p>
     * Taken from <a href="http://blog.kornr.net/index.php/2008/12/09/understanding-the-osgi-logging-service">
     * http://blog.kornr.net/index.php/2008/12/09/understanding-the-osgi-logging-service</a>
     * </p>
     */
    private ServiceListener m_servlistener;

    /**
     * Async log event forwarding. Does not automatically register.
     * 
     * @param eventAdmin
     *            the EventAdmin
     * @see #register()
     * @see #unregister()
     */
    public EventForwardingLogListener(BundleContext context, EventAdmin eventAdmin)
    {
        this(context, eventAdmin, true);
    }

    /**
     * Does not automatically register.
     * 
     * @param eventAdmin
     *            the EventAdmin
     * @param async
     *            whether or not to forward log events async
     * @see #register()
     * @see #unregister()
     */
    public EventForwardingLogListener(BundleContext context, EventAdmin eventAdmin, boolean async)
    {
        super(eventAdmin, async);
        this.context = context;
        m_servlistener = new LogServiceListener();
    }

    public synchronized void unregister()
    {
        context.removeServiceListener(m_servlistener);
        for (LogReaderService lrs : m_readers)
        {
            try
            {
                lrs.removeLogListener(this);
            }
            catch (Exception e)
            {
            }
        }
        m_readers.clear();
    }

    public synchronized void register()
    {
        // Get a list of all the registered LogReaderService, and add the console listener
        ServiceTracker<LogReaderService, LogReaderService> logReaderTracker = new ServiceTracker<LogReaderService, LogReaderService>(
                context, LogReaderService.class, null);
        logReaderTracker.open();
        Object[] readers = logReaderTracker.getServices();
        if (readers != null)
        {
            for (int i = 0; i < readers.length; i++)
            {
                LogReaderService lrs = (LogReaderService) readers[i];
                m_readers.add(lrs);
                lrs.addLogListener(this);
            }
        }

        // logReaderTracker.close();

        // Add the ServiceListener, but with a filter so that we only receive events related to LogReaderService
        String filter = '(' + Constants.OBJECTCLASS + '=' + LogReaderService.class.getName() + ')';
        ServiceTracker<LogService, LogService> logServiceTracker = new ServiceTracker<LogService, LogService>(context,
                LogService.class, null);
        logServiceTracker.open();
        LogService logservice = logServiceTracker.getService();
        logServiceTracker.close();
        try
        {
            context.addServiceListener(m_servlistener, filter);
            logservice.log(IStatus.OK, "Registered EventForwardingLogListener for OSGi log events"); //$NON-NLS-1$
        }
        catch (InvalidSyntaxException e)
        {
            logservice.log(IStatus.OK, "Error while registering EventForwardingLogListener for OSGi log events", e); //$NON-NLS-1$
        }
    }

    /**
     * Whether to send or post log events.
     * 
     * @param async
     */
    public void setAsnycLogging(boolean async)
    {
        asyncLogging = async;
    }

    @Override
    public void logged(LogEntry entry)
    {
        if (eventAdmin != null)
        {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(ATTR_BUNDLE_SYMBOLICNAME, entry.getBundle().getSymbolicName());
            map.put(ATTR_LEVEL, Integer.valueOf(entry.getLevel()));
            map.put(ATTR_LEVEL_TYPE, ATTR_LEVEL_TYPE_OSGi);
            map.put(ATTR_MESSAGE, entry.getMessage());
            Throwable t = entry.getException();
            if (t != null)
            {
                t.getStackTrace();
                map.put(ATTR_EXCEPTION, t);
            }
            Event event = new Event(TOPIC, map);
            if (asyncLogging)
                eventAdmin.postEvent(event);
            else
                eventAdmin.sendEvent(event);
        }
    }
}
