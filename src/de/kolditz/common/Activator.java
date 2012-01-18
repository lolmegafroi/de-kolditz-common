/**
 * 
 */
package de.kolditz.common;

import org.apache.log4j.BasicConfigurator;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public class Activator extends AbstractUIPlugin {
    public static final String PLUGIN_ID = "de.kolditz.rcp"; //$NON-NLS-1$

    private static Activator instance;

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        instance = this;
        BasicConfigurator.configure();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        instance = null;
        super.stop(context);
    }

    public static Activator getDefault() {
        return instance;
    }

    /**
     * Returns the Plugin's {@link IPreferenceStore}
     * 
     * @return the Plugin's {@link IPreferenceStore}
     */
    public static IPreferenceStore getStore() {
        return getDefault().getPreferenceStore();
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in relative path
     * 
     * @param path
     *            the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    public static void log(int severity, String message) {
        getDefault().getLog().log(new Status(severity, PLUGIN_ID, message));
    }

    public static void log(int severity, String message, Throwable exception) {
        getDefault().getLog().log(new Status(severity, PLUGIN_ID, message, exception));
    }
}
