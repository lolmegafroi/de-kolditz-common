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
 * @author Till Kolditz - Till.Kolditz@gmail.com
 */
public class ConfigurationFileNotWritableException extends RuntimeException
{
    /**
     * 
     */
    private static final long serialVersionUID = -7201417355430125262L;

    private static final String MESSAGE = "application configuration file is not writable: ";

    /**
     * 
     */
    public ConfigurationFileNotWritableException(File confFile)
    {
        super(MESSAGE + confFile.getAbsolutePath());
    }

    public ConfigurationFileNotWritableException(File confFile, Throwable cause)
    {
        super(MESSAGE + confFile.getAbsolutePath(), cause);
    }
}
