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
package de.kolditz.common.ui.i18n;

import java.util.ResourceBundle;

import de.kolditz.common.i18n.Messages;

/**
 * @author Till Kolditz - Till.Kolditz@gmail.com
 */
public class I18N extends Messages
{
    public static final String CONTROLS_IPTEXT_INVALID = "Controls.IPText.Invalid"; //$NON-NLS-1$
    public static final String CONTROLS_PORTTEXT_INVALID = "Controls.PortText.Invalid"; //$NON-NLS-1$

    public static final String DIALOGS_ERRORDIALOG_TITLE = "Dialogs.ErrorDialog.Title"; //$NON-NLS-1$

    public static final String FIELDS_FILEFIELD_FILENOTSET = "Fields.FileField.FNS"; //$NON-NLS-1$
    public static final String FIELDS_FILEFIELD_FILEDOESNOTEXIST = "Fields.FileField.FDNE"; //$NON-NLS-1$
    public static final String FIELDS_FILEFIELD_OVERWRITEDIALOG_MSG = "Fields.FileField.OWD.Msg"; //$NON-NLS-1$
    public static final String FIELDS_FILEFIELD_OVERWRITEDIALOG_TITLE = "Fields.FileField.OWD.Title"; //$NON-NLS-1$

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
