package eu.europeana.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    public Manager(String defaultPropertiesPath) {
        this.defaultPropertiesPath = defaultPropertiesPath;
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
}
