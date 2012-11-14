/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 * created on 26.09.2012 at 17:55:42
 * 
 *  Contributors:
 *      Till Kolditz
 *******************************************************************************/
package de.kolditz.common.ui.rcp;

import java.util.Hashtable;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.swt.custom.StyledText;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import de.kolditz.common.osgi.EventForwardingLogListener;
import de.kolditz.common.osgi.LogEventForwarder;
import de.kolditz.common.ui.widgets.Log4jTextAppender;

/**
 * Listens to OSGi LogReaderService log events and appends them as log4j log entries. Also listens to events from the
 * {@link EventForwardingLogListener}.
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public class OSGiTextAppender extends Log4jTextAppender implements LogListener, EventHandler
{
    private BundleListener bundleListener = new BundleListener()
    {
        @Override
        public void bundleChanged(BundleEvent event)
        {
            switch (event.getType())
            {
                case BundleEvent.STOPPING:
                case BundleEvent.STOPPED:
                    handleStopped();
                    break;

                case BundleEvent.STARTED:
                    handleStarted();
                    break;
            }
        }

        private void handleStopped()
        {
            if (srvReg != null)
                srvReg.unregister();
            srvReg = null;
            if (log != null)
                log.removeLogListener(OSGiTextAppender.this);
            log = null;
        }

        private void handleStarted()
        {
            if (srvReg == null)
            {
                Hashtable<String, Object> ht = new Hashtable<String, Object>();
                ht.put(EventConstants.EVENT_TOPIC, EventForwardingLogListener.TOPIC);
                srvReg = context.registerService(EventHandler.class, OSGiTextAppender.this, ht);
            }
            if (log == null)
            {
                log = logTracker.getService();
                if (log != null)
                {
                    log.addLogListener(OSGiTextAppender.this);
                }
            }
        }
    };

    private BundleContext context;
    private ServiceRegistration<EventHandler> srvReg;
    private ServiceTracker<LogReaderService, LogReaderService> logTracker;
    private LogReaderService log;

    public OSGiTextAppender(BundleContext context)
    {
        super();
        this.context = context;
        init();
    }

    public OSGiTextAppender(BundleContext context, StyledText text)
    {
        super(text);
        this.context = context;
        init();
    }

    private void init()
    {
        context.addBundleListener(bundleListener);
        Hashtable<String, Object> ht = new Hashtable<String, Object>();
        ht.put(EventConstants.EVENT_TOPIC, EventForwardingLogListener.TOPIC);
        srvReg = context.registerService(EventHandler.class, this, ht);
        logTracker = new ServiceTracker<LogReaderService, LogReaderService>(context, LogReaderService.class, null);
        logTracker.open();
        log = logTracker.getService();
        if (log != null)
        {
            log.addLogListener(this);
        }
    }

    @Override
    public void logged(LogEntry entry)
    {
        Logger.getLogger(entry.getBundle().getSymbolicName()).log(levelFromOSGi(entry.getLevel()), entry.getMessage(),
                entry.getException());
    }

    /**
     * @param osgiLevel
     * @return the log4j level
     */
    private Level levelFromOSGi(int osgiLevel)
    {
        switch (osgiLevel)
        {
            case LogService.LOG_DEBUG:
                return Level.DEBUG;

            case LogService.LOG_INFO:
                return Level.INFO;

            case LogService.LOG_WARNING:
                return Level.WARN;

            case LogService.LOG_ERROR:
                return Level.ERROR;

            default:
                return Level.ALL;
        }
    }

    @Override
    public void handleEvent(Event event)
    {
        String bundleSymbName = (String) event.getProperty(LogEventForwarder.ATTR_BUNDLE_SYMBOLICNAME);
        if (bundleSymbName != null)
        {
            int level = (Integer) event.getProperty(LogEventForwarder.ATTR_LEVEL);
            Level lvl = Level.ALL;
            String levelType = (String) event.getProperty(LogEventForwarder.ATTR_LEVEL_TYPE);
            if (levelType.equals(LogEventForwarder.ATTR_LEVEL_TYPE_OSGi))
            {
                lvl = levelFromOSGi(level);
            }
            else if (levelType.equals(LogEventForwarder.ATTR_LEVEL_TYPE_LOG4J))
            {
                lvl = Level.toLevel(level);
            }
            String msg = (String) event.getProperty(LogEventForwarder.ATTR_MESSAGE);
            Throwable t = (Throwable) event.getProperty(LogEventForwarder.ATTR_EXCEPTION);
            Logger.getLogger(bundleSymbName).log(lvl, msg, t);
        }
    }
}
