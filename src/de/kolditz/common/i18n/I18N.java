/**
 * created 09.11.2011 15:48:03
 */
package de.kolditz.common.i18n;

import java.util.ResourceBundle;

/**
 * 
 * @author <a href="mailto:Till.Kolditz@GoogleMail.com">Till Kolditz &lt;Till.Kolditz@GoogleMail.com&gt;</a>
 * 
 */
public class I18N extends Messages
{
    public static final String UIUTILS_EMD_EXPCOMP_TITLE = "UIUTILS_EMD_EXPCOMP_TITLE"; //$NON-NLS-1$

    /**
     * the singleton instance
     */
    private static volatile I18N instance = new I18N();

    /**
     * not instantiable from outside
     */
    private I18N()
    {
        super(ResourceBundle.getBundle(I18N.class.getName()));
    }

    /**
     * @return the instance
     */
    public static I18N get()
    {
        return instance;
    }
}
