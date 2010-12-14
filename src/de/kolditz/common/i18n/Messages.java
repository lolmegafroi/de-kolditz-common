/*
 * jExam 2003
 * Created on 24.11.2003
 *
 */
package de.kolditz.common.i18n;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

/**
 * <p>Copied from the jExam project: <a href="http://www.jexam.de">www.jexam.de</a>
 * </p>
 * <p>Provides the functionality of a <code>ResourceBundle</code> for this plugin. This class
 * can not be initialized.
 * </p> 
 * 
 * @author Thomas Förster - Thomas.Foerster@jexam.de
 * @author Dirk Buchhorn - Dirk.Buchhorn@jexam.de
 */
public class Messages
{
    protected ResourceBundle bundle;

    /**
     * 
     */
    public Messages(final ResourceBundle b)
    {
        super();
        bundle = b;
    }

    /**
     * Returns the formatted message for the given key in
     * the resource bundle. 
     *
     * @param key the resource name
     * @param args the message arguments
     * @return the string
     */
    public String format(final String key, final Object[] args)
    {
        final MessageFormat format = new MessageFormat(this.getString(key));
        return format.format(args);
    }

    /**
     * Returns the resource object with the given key in
     * the resource bundle. If there isn't any value under
     * the given key, the key is returned.
     *
     * @param key the resource name
     * @return the string
     */
    public String getString(final String key)
    {
        try
        {
            return bundle.getString(key);
        }
        catch(final MissingResourceException e)
        {
            return key;
        }
    }

    /**
     * Returns the resource object with the given key in
     * the resource bundle. If there isn't any value under
     * the given key, the default value is returned.
     *
     * @param key the resource name
     * @param def the default value
     * @return the string
     */
    public String getString(final String key, final String def)
    {
        try
        {
            return bundle.getString(key);
        }
        catch(final MissingResourceException e)
        {
            return def;
        }
    }

    /**
     * Returns the resource object with the given key in
     * the resource bundle. This is a comma-separated list. An Array of
     * this is returned.
     * 
     * @param key
     * @return Object[] - the list as array
     */
    public Object[] getArray(final String key)
    {
        return getList(key).toArray();
    }

    /**
     * Returns the resource object with the given key in
     * the resource bundle. This is a comma-separated list. An Array of
     * Strings is returned.
     * 
     * @param key
     * @return String[] - the list as String-array
     */
    public String[] getStringArray(final String key)
    {
        return getList(key).toArray(new String[getList(key).size()]);
    }

    /**
     * Returns the resource object with the given key in
     * the resource bundle. This is a comma-separated list. 
     * 
     * @param key
     * @return List - the list
     */
    public List<String> getList(final String key)
    {
        final String value = this.getString(key);
        final StringTokenizer tokenizer = new StringTokenizer(value, ","); //$NON-NLS-1$
        final List<String> list = new ArrayList<String>();
        while(tokenizer.hasMoreElements())
        {
            list.add(tokenizer.nextToken());
        }
        return list;
    }
}
