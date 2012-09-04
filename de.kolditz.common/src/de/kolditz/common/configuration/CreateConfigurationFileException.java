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
package de.kolditz.common.configuration;

import java.io.File;

/**
 * 
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public class CreateConfigurationFileException extends RuntimeException
{
    /**
     * 
     */
    private static final long serialVersionUID = 8012590926561357727L;

    private static final String MESSAGE = "could not create application configuration file ";

    /**
     * 
     */
    public CreateConfigurationFileException(File confFile)
    {
        super(MESSAGE + confFile.getAbsolutePath());
    }

    public CreateConfigurationFileException(File confFile, Throwable cause)
    {
        super(MESSAGE + confFile.getAbsolutePath(), cause);
    }
}
