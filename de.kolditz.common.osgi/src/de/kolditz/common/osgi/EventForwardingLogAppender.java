/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 * created on 26.09.2012 at 19:29:08
 * 
 *  Contributors:
 *      Till Kolditz
 *******************************************************************************/
package de.kolditz.common.osgi;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 *
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public class EventForwardingLogAppender extends LogEventForwarder implements Appender
{
    private boolean asyncLogging;

    /**
     * Async log event forwarding.
     * 
     * @param eventAdmin the EventAdmin
     */
    public EventForwardingLogAppender(EventAdmin eventAdmin)
    {
        this(eventAdmin, true);
    }

    /**
     * 
     * @param eventAdmin the EventAdmin
     * @param async whether or not to forward log events async
     */
    public EventForwardingLogAppender(EventAdmin eventAdmin, boolean async)
    {
        super(eventAdmin, async);
    }

    @Override
    public void close()
    {
    }

    @Override
    public boolean requiresLayout()
    {
        return false;
    }

    @Override
    public void addFilter(Filter newFilter)
    {
    }

    @Override
    public Filter getFilter()
    {
        return null;
    }

    @Override
    public void clearFilters()
    {
    }

    @Override
    public void doAppend(LoggingEvent event)
    {
        if(eventAdmin != null)
        {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(ATTR_BUNDLE_SYMBOLICNAME, event.getLoggerName());
            map.put(ATTR_LEVEL, Integer.valueOf(event.getLevel().toInt()));
            map.put(ATTR_LEVEL_TYPE, ATTR_LEVEL_TYPE_LOG4J);
            map.put(ATTR_MESSAGE, event.getMessage());
            ThrowableInformation ti = event.getThrowableInformation();
            if(ti != null)
            {
                Throwable t = ti.getThrowable();
                t.getStackTrace();
                map.put(ATTR_EXCEPTION, t);
            }
            Event actualEvent = new Event(TOPIC, map);
            if(asyncLogging)
                eventAdmin.postEvent(actualEvent);
            else
                eventAdmin.sendEvent(actualEvent);
        }
    }

    @Override
    public String getName()
    {
        return null;
    }

    @Override
    public void setErrorHandler(ErrorHandler errorHandler)
    {
    }

    @Override
    public ErrorHandler getErrorHandler()
    {
        return null;
    }

    @Override
    public void setLayout(Layout layout)
    {
    }

    @Override
    public Layout getLayout()
    {
        return null;
    }

    @Override
    public void setName(String name)
    {
    }
}
