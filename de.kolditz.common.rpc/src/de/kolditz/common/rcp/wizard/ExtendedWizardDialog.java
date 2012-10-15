/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 * created on 15.10.2012 at 20:35:26
 * 
 *  Contributors:
 *      Till Kolditz
 *******************************************************************************/
package de.kolditz.common.rcp.wizard;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

/**
 *
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public class ExtendedWizardDialog extends WizardDialog
{
    private IDialogSettings settings;

    /**
     * @param parentShell
     * @param newWizard
     */
    public ExtendedWizardDialog(Shell parentShell, IWizard newWizard)
    {
        super(parentShell, newWizard);
    }

    public void setDialogBoundsSettings(IDialogSettings settings)
    {
        this.settings = settings;
    }

    protected IDialogSettings getDialogBoundsSettings()
    {
        return settings;
    }
}
