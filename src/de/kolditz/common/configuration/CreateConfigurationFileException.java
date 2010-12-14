/**
 * 
 */
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
