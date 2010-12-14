/**
 * 
 */
package de.kolditz.common.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

/**
 * 
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public class Configuration
{
    /**
     * the file extension of the configuration file (".conf")
     */
    public static final String CONFIG_FILE_EXT = ".conf";

    protected Properties properties;
    protected String filename;
    protected File confFile;

    /**
     * 
     * @param filename the config file's name (without the file extension)
     * 
     * @throws CreateConfigurationFileException
     * @throws FileNotFoundException
     * @throws IOException
     * 
     * @see {@link #CONFIG_FILE_EXT}
     * @see Properties
     */
    public Configuration(String filename) throws FileNotFoundException, IOException
    {
        this(filename, null);
    }

    /**
     * 
     * @param filename the config file's name (without the file extension)
     * @param defaults the Properties defaults
     * 
     * @throws CreateConfigurationFileException
     * @throws FileNotFoundException
     * @throws IOException
     * 
     * @see {@link #CONFIG_FILE_EXT}
     * @see Properties
     */
    public Configuration(String filename, Properties defaults) throws FileNotFoundException, IOException
    {
        assert filename != null;
        assert filename.isEmpty() == false;

        this.filename = filename + CONFIG_FILE_EXT;
        String currentPath = System.getProperty("user.dir");
        System.out.println(currentPath);
        File appDir = new File(currentPath);

        if(!appDir.exists())
        {
            throw new RuntimeException("the environment variable \"user.dir\" does not point to a directory");
        }

        confFile = new File(appDir, this.filename);
        checkConfFileExistence();

        properties = new Properties(defaults);
        properties.load(new FileReader(confFile));
    }

    private void checkConfFileExistence()
    {
        if(!confFile.exists())
        {
            try
            {
                confFile.createNewFile();
            }
            catch(Exception ex)
            {
                throw new CreateConfigurationFileException(confFile, ex);
            }
        }
    }

    public String getProperty(String key)
    {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue)
    {
        return properties.getProperty(key, defaultValue);
    }

    public String setProperty(String key, String value)
    {
        return (String)properties.setProperty(key, value);
    }

    public Set<String> getPropertyNames()
    {
        return properties.stringPropertyNames();
    }

    public Map<String, String> getProperties()
    {
        Map<String, String> result = new HashMap<String, String>();
        for(Entry<Object, Object> entry : properties.entrySet())
        {
            result.put(entry.getKey().toString(), entry.getValue().toString());
        }
        return result;
    }

    /**
     * store the configuration in the file specified in the constructor
     * 
     * @param comment may be null
     */
    public void save(String comment) throws IOException
    {
        checkConfFileExistence();
        FileWriter writer = new FileWriter(confFile);
        properties.store(writer, comment);
    }

    public void reload()
    {
    }

    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();
    }
}
