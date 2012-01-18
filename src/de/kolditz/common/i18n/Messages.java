/*
 * jExam 2003
 * Created on 24.11.2003
 *
 */
package de.kolditz.common.i18n;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.ListenerList;

import de.kolditz.common.ui.UIUtils;

/**
 * <p>
 * Provides the functionality of a <code>ResourceBundle</code> for this plugin.
 * </p>
 */
public class Messages {
    public static interface ResourceBundleUpdatedListener {
        /**
         * Just a notification without any data since this is assumed to be used on updating labels in a UI. Beware that
         * this might happen in any thread!
         */
        void resourceBundleUpdated();
    }

    /**
     * the Logger
     */
    private Logger logger = Logger.getLogger(Messages.class);

    protected volatile ResourceBundle bundle;

    protected Object lock;

    protected volatile ListenerList resourceBundleUpdatedListeners = new ListenerList();

    /**
     * 
     */
    public Messages(final ResourceBundle b) {
        super();
        bundle = b;
    }

    /**
     * Update the locale to be used.
     * 
     * @param baseName
     * @param locale
     */
    public void setLocale(String baseName, Locale locale) {
        synchronized (lock) {
            try {
                ResourceBundle newRB = ResourceBundle.getBundle(baseName, locale);
                bundle = newRB;
                notifyResourceBundleUpdatedListeners();
            } catch (MissingResourceException e) {
                logger.log(Level.ERROR, e.getMessage(), e);
                UIUtils.openError(e);
            }
        }
    }

    /**
     * Returns the formatted message for the given key in the resource bundle.
     * 
     * @param key
     *            the resource name
     * @param args
     *            the message arguments
     * @return the string
     */
    public String format(final String key, final Object... args) {
        final MessageFormat format = new MessageFormat(getString(key));
        return format.format(args);
    }

    /**
     * Returns the resource object with the given key in the resource bundle. If there isn't any value under the given
     * key, the key is returned.
     * 
     * @param key
     *            the resource name
     * @return the string
     */
    public String getString(final String key) {
        try {
            return bundle.getString(key);
        } catch (final MissingResourceException e) {
            logger.log(Level.WARN, e.getMessage(), e);
            return key;
        }
    }

    /**
     * Returns the resource object with the given key in the resource bundle. If there isn't any value under the given
     * key, the default value is returned.
     * 
     * @param key
     *            the resource name
     * @param def
     *            the default value
     * @return the string
     */
    public String getString(final String key, final String def) {
        try {
            return bundle.getString(key);
        } catch (final MissingResourceException e) {
            return def;
        }
    }

    /**
     * Returns the resource object with the given key in the resource bundle. This is a comma-separated list. An Array
     * of this is returned.
     * 
     * @param key
     *            the key
     * @return Object[] - the list as array
     */
    public Object[] getArray(final String key) {
        return getList(key).toArray();
    }

    /**
     * Returns the resource object with the given key in the resource bundle. This is a comma-separated list. An Array
     * of Strings is returned.
     * 
     * @param key
     *            the key
     * @return String[] - the list as String-array
     */
    public String[] getStringArray(final String key) {
        return getList(key).toArray(new String[getList(key).size()]);
    }

    /**
     * Returns the resource object with the given key in the resource bundle. This is a comma-separated list.
     * 
     * @param key
     *            the key
     * @return List - the list
     */
    public List<String> getList(final String key) {
        return getList(key, ","); //$NON-NLS-1$
    }

    /**
     * Returns the resource object with the given key in the resource bundle. This is a delim-separated list.
     * 
     * @param key
     *            the key
     * @param delim
     *            the entry delimiter
     * @return List - the list
     */
    public List<String> getList(final String key, final String delim) {
        final String value = this.getString(key);
        final StringTokenizer tokenizer = new StringTokenizer(value, delim);
        final List<String> list = new ArrayList<String>();
        while (tokenizer.hasMoreElements()) {
            list.add(tokenizer.nextToken());
        }
        return list;
    }

    private void notifyResourceBundleUpdatedListeners() {
        for (Object o : resourceBundleUpdatedListeners.getListeners()) {
            ((ResourceBundleUpdatedListener) o).resourceBundleUpdated();
        }
    }

    public void addResoruceBundleUpdatedListener(ResourceBundleUpdatedListener listener) {
        resourceBundleUpdatedListeners.add(listener);
    }

    public void removeResoruceBundleUpdatedListener(ResourceBundleUpdatedListener listener) {
        resourceBundleUpdatedListeners.remove(listener);
    }
}
