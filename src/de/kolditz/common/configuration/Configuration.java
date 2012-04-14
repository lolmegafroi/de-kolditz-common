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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

/**
 * 
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public class Configuration {
    /**
     * the file extension of the configuration file (".conf")
     */
    public static final String CONFIG_FILE_EXT = ".conf";

    protected Properties properties;
    protected String filename;
    protected File confFile;

    /**
     * 
     * @param filename
     *            the config file's name (without the file extension)
     * 
     * @throws CreateConfigurationFileException
     * @throws FileNotFoundException
     * @throws IOException
     * 
     * @see {@link #CONFIG_FILE_EXT}
     * @see Properties
     */
    public Configuration(String filename) throws FileNotFoundException, IOException {
        this(filename, null);
    }

    /**
     * 
     * @param baseName
     *            the config file's base name (without the file extension)
     * @param defaults
     *            the Properties defaults
     * 
     * @throws CreateConfigurationFileException
     * @throws FileNotFoundException
     * @throws IOException
     * 
     * @see {@link #CONFIG_FILE_EXT}
     * @see Properties
     */
    public Configuration(String baseName, Properties defaults) throws FileNotFoundException, IOException {
        assert baseName != null;
        assert baseName.isEmpty() == false;

        this.filename = baseName + CONFIG_FILE_EXT;
        String currentPath = System.getProperty("user.dir");
        System.out.println(currentPath);
        File appDir = new File(currentPath);

        if (!appDir.exists()) {
            throw new RuntimeException("the environment variable \"user.dir\" does not point to a directory");
        }

        confFile = new File(appDir, this.filename);
        checkConfFileExistence();

        properties = new Properties(defaults);
        FileReader fd = new FileReader(confFile);
        properties.load(fd);
        fd.close();
    }

    private void checkConfFileExistence() {
        if (!confFile.exists()) {
            try {
                confFile.createNewFile();
            } catch (Exception ex) {
                throw new CreateConfigurationFileException(confFile, ex);
            }
        }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public long getLong(String key) {
        return Long.valueOf(get(key));
    }

    public int getInt(String key) {
        return Integer.valueOf(get(key));
    }

    public char getChar(String key) {
        return get(key).charAt(0);
    }

    public boolean getBool(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public float getFloat(String key) {
        return Float.valueOf(get(key));
    }

    public double getDouble(String key) {
        return Double.valueOf(get(key));
    }

    public String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public void set(String key, String value) {
        properties.setProperty(key, value);
    }

    public void set(String key, int value) {
        properties.setProperty(key, Integer.toString(value));
    }

    public void set(String key, long value) {
        properties.setProperty(key, Long.toString(value));
    }

    public void set(String key, boolean value) {
        properties.setProperty(key, Boolean.toString(value));
    }

    public void set(String key, float value) {
        properties.setProperty(key, Float.toString(value));
    }

    public void set(String key, double value) {
        properties.setProperty(key, Double.toString(value));
    }

    public Set<String> getPropertyNames() {
        return properties.stringPropertyNames();
    }

    public Map<String, String> getProperties() {
        Map<String, String> result = new HashMap<String, String>();
        for (Entry<Object, Object> entry : properties.entrySet()) {
            result.put(entry.getKey().toString(), entry.getValue().toString());
        }
        return result;
    }

    /**
     * store the configuration in the file specified in the constructor
     * 
     * @param comment
     *            may be null
     */
    public void save(String comment) throws IOException {
        checkConfFileExistence();
        FileWriter writer = new FileWriter(confFile);
        properties.store(writer, comment);
    }

    public void reload() {
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
