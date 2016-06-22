package eu.europeana.common;

import eu.europeana.inspire.common.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;

/**
 * @author Simon Tzanakis (Simon.Tzanakis@europeana.eu)
 * @since 2016-06-21
 */
public class Manager {
    private static final Logger logger = LogManager.getLogger();
    private final static String accessUrl_key = "accessUrl";
    private final static String configurationFileName = "configuration-inspire.properties";
    private final static String storageDirectoryKey = "storageDirectory";
    private static String defaultPropertiesPath;
    private String rootStorageDirectory;

    public Manager(String defaultPropertiesPath) throws FileNotFoundException, ConfigurationException {
        this.defaultPropertiesPath = defaultPropertiesPath;
        PropertiesConfiguration propertiesConfigurationInspire = Configuration.loadConfiguration(Manager.getDefaultPropertiesPath(), Manager.getConfigurationFileName());
        rootStorageDirectory = propertiesConfigurationInspire.getProperty(Manager.getStorageDirectoryKey()).toString();
    }

    public static Logger getLogger() {
        return logger;
    }

    public static String getAccessUrl_key() {
        return accessUrl_key;
    }

    public static String getConfigurationFileName() {
        return configurationFileName;
    }

    public static String getDefaultPropertiesPath() {
        return defaultPropertiesPath;
    }

    public static String getStorageDirectoryKey() {
        return storageDirectoryKey;
    }

    public String getRootStorageDirectory() {
        return rootStorageDirectory;
    }
}
