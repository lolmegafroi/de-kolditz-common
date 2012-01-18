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
public class ConfigurationFileNotWritableException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = -7201417355430125262L;

    private static final String MESSAGE = "application configuration file is not writable: ";

    /**
     * 
     */
    public ConfigurationFileNotWritableException(File confFile) {
        super(MESSAGE + confFile.getAbsolutePath());
    }

    public ConfigurationFileNotWritableException(File confFile, Throwable cause) {
        super(MESSAGE + confFile.getAbsolutePath(), cause);
    }
}
