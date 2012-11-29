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

import java.util.Map;

import org.eclipse.equinox.app.IApplication;
import org.osgi.service.event.EventHandler;

/**
 * The application life cycle interface used for remote controlling the OSGi application itself (e.g. stopping or
 * restarting).
 * 
 * @author Till Kolditz - Till.Kolditz@gmail.com
 */
public interface IOSGiApplicationLifeCycle
{
    public static final String EVENT_PROPERTY = "data";        //$NON-NLS-1$

    public static final String EVENT_STARTED = "started";     //$NON-NLS-1$

    public static final String EVENT_CLOSE = "close";       //$NON-NLS-1$

    public static final String EVENT_CLOSE_FORCED = "close_forced"; //$NON-NLS-1$

    public static final String EVENT_RESTART = "restart";     //$NON-NLS-1$

    public static final String EVENT_RELAUNCH = "relaunch";    //$NON-NLS-1$

    /**
     * Tries to gracefully close the OSGi application
     * 
     * @return the status of whether graceful shutdown succeeded
     * @see IApplication#EXIT_OK
     */
    void close();

    /**
     * Tries to gracefully close and restart the OSGi application
     * 
     * @return the status of whether graceful shutdown succeeded
     * @see IApplication#EXIT_RESTART
     */
    void restart();

    /**
     * Tries to gracefully close and relaunch the OSGi application.
     * 
     * @param relaunchProperties
     *            the relaunch eclipse properties
     * @see IApplication#EXIT_RELAUNCH
     */
    void relaunch(Map<String, String> relaunchProperties);

    /**
     * Forces terminating the VM ({@link System#exit(int)}).
     */
    void forceClose();

    /**
     * @return the topic on which {@link EventHandler}s shall listen to.
     */
    String getEventTopic();

    /**
     * Makes this life cycle send a test message through the OSGi Event Bus which should be sent to the remote instance
     * as well
     * 
     * @param message
     *            the test message
     */
    void testEvent(String message);
}
