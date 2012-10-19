/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 * created on 26.09.2012 at 19:42:51
 * 
 *  Contributors:
 *      Till Kolditz
 *******************************************************************************/
package de.kolditz.common.osgi;

import org.osgi.service.event.EventAdmin;

/**
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public abstract class LogEventForwarder
{
    public static final String TOPIC = "org/osgi/logging";            //$NON-NLS-1$

    /**
     * the logging bundle's symbolic name or the logger's name (String)
     */
    public static final String ATTR_BUNDLE_SYMBOLICNAME = "logentry.bundle.symbolicName"; //$NON-NLS-1$
    /**
     * the level (Integer)
     */
    public static final String ATTR_LEVEL = "logentry.level";              //$NON-NLS-1$

    /**
     * The level type, log4j or OSGi
     */
    public static final String ATTR_LEVEL_TYPE = "logentry.leveltype";          //$NON-NLS-1$
    public static final String ATTR_LEVEL_TYPE_LOG4J = "log4j";                       //$NON-NLS-1$
    public static final String ATTR_LEVEL_TYPE_OSGi = "OSGi";                        //$NON-NLS-1$

    /**
     * the message (String)
     */
    public static final String ATTR_MESSAGE = "logentry.message";            //$NON-NLS-1$
    /**
     * the exception (Throwable)
     */
    public static final String ATTR_EXCEPTION = "logentry.exception";          //$NON-NLS-1$

    protected EventAdmin eventAdmin;
    protected boolean asyncLogging;

    /**
     * Async log event forwarding.
     * 
     * @param eventAdmin
     *            the EventAdmin
     */
    public LogEventForwarder(EventAdmin eventAdmin)
    {
        this(eventAdmin, true);
    }

    /**
     * @param eventAdmin
     *            the EventAdmin
     * @param async
     *            whether or not to forward log events async
     */
    public LogEventForwarder(EventAdmin eventAdmin, boolean async)
    {
        this.eventAdmin = eventAdmin;
        this.asyncLogging = async;
    }
}
