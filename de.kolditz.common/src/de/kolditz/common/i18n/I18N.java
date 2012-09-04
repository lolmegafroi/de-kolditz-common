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

    public static final String IPTEXT_INVALID = "IPTEXT_INVALID"; //$NON-NLS-1$

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
