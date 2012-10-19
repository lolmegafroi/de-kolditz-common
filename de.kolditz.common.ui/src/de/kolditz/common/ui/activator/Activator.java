package de.kolditz.common.ui.activator;

import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Till Kolditz - Till.Kolditz@gmail.com
 */
public class Activator extends AbstractUIPlugin
{
    public static final String PLUGIN_ID = "de.kolditz.common.ui"; //$NON-NLS-1$

    private static Activator plugin;

    private static BundleContext context;

    public static Activator getDefault()
    {
        return plugin;
    }

    public static BundleContext getContext()
    {
        return context;
    }

    public static void log(int severity, String message)
    {
        plugin.getLog().log(new Status(severity, PLUGIN_ID, message));
    }

    public static void log(int severity, String message, Throwable exception)
    {
        plugin.getLog().log(new Status(severity, PLUGIN_ID, message, exception));
    }

    @Override
    public void start(BundleContext bundleContext) throws Exception
    {
        Activator.context = bundleContext;
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception
    {
        Activator.context = null;
    }
}
