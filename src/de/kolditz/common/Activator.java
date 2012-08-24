/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     Till Kolditz
 *******************************************************************************/
package de.kolditz.common;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * The activator
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public class Activator extends Plugin {
    public static final String PLUGIN_ID = "de.kolditz.rcp"; //$NON-NLS-1$

    private static Activator instance;

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        instance = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        instance = null;
        super.stop(context);
    }

    public static Activator getDefault() {
        return instance;
    }

    public static void log(int severity, String message) {
        getDefault().getLog().log(new Status(severity, PLUGIN_ID, message));
    }

    public static void log(int severity, String message, Throwable exception) {
        getDefault().getLog().log(new Status(severity, PLUGIN_ID, message, exception));
    }
}
