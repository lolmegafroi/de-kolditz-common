package de.kolditz.common.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;

/**
 * 
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public class UIUtils
{

    public static void openError(Exception e)
    {
        ErrorDialog.openError(Display.getCurrent().getActiveShell(), e.getClass().getSimpleName(), e.getMessage(),
                new Status(IStatus.ERROR, "mp3Converter", e.getMessage(), e));
    }
}
