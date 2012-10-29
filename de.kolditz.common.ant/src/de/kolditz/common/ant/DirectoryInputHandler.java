/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 * created on 29.10.2012 at 17:25:44
 * 
 *  Contributors:
 *      Till Kolditz
 *******************************************************************************/
package de.kolditz.common.ant;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.input.InputHandler;
import org.apache.tools.ant.input.InputRequest;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * An ant input handler for choosing a single folder.
 *
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public class DirectoryInputHandler implements InputHandler
{
    @Override
    public void handleInput(InputRequest request) throws BuildException
    {
        Display display = Display.getDefault();
        Shell shell = new Shell(display);
        StringBuilder sb = new StringBuilder("Select a directory.\n");
        String def = request.getDefaultValue();
        File file = null;
        if(def != null)
        {
            if(def.length() == 0)
            {
                def = null;
            }
            else
            {
                file = new File(def);
            }
        }
        if(file != null)
        {
            try
            {
                def = file.getCanonicalPath();
            }
            catch(IOException e)
            {
                System.err.println("[DirectoryInputHandler] given directory in defaultValue does not exist");
                def = null;
            }
        }
        else
        {
            def = null;
        }
        if(def != null)
        {
            sb.append("Default is: ").append(def).append("\nWhen cancelling, the default directory is used");
        }
        else
        {
            sb.append("You must select a directory or cancelling this dialog will cancel the while build");
        }
        DirectoryDialog dialog = new DirectoryDialog(shell, SWT.SHEET | SWT.SYSTEM_MODAL);
        dialog.setText("Ant Directory Selector");
        if(def != null) dialog.setFilterPath(def);
        dialog.setMessage(sb.toString());
        String dir = dialog.open();
        shell.dispose();
        display.dispose();
        if(dir == null) dir = def;
        if(dir == null) throw new BuildException("no directory selected");
        request.setInput(dir);
    }
}
