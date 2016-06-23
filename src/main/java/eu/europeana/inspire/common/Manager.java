package eu.europeana.common;

import eu.europeana.inspire.common.Configuration;
import eu.europeana.inspire.common.ImagesProcessor;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
    public static AccessorsManager accessorsManager;

    public Manager(String defaultPropertiesPath, String pinterestConfDirectory) throws IOException, ConfigurationException {
        this.defaultPropertiesPath = defaultPropertiesPath;
        PropertiesConfiguration propertiesConfigurationInspire = Configuration.loadConfiguration(Manager.getDefaultPropertiesPath(), Manager.getConfigurationFileName());
        rootStorageDirectory = propertiesConfigurationInspire.getProperty(Manager.getStorageDirectoryKey()).toString();

        Manager.accessorsManager = new AccessorsManager(pinterestConfDirectory);

        //Create required directories
        Files.createDirectories(Paths.get(rootStorageDirectory, ImagesProcessor.directoryOriginalSizeName));
        Files.createDirectories(Paths.get(rootStorageDirectory, ImagesProcessor.directory100x100Name));
        Files.createDirectories(Paths.get(rootStorageDirectory, ImagesProcessor.directory60x60Name));
        Files.createDirectories(Paths.get(rootStorageDirectory, ImagesProcessor.directory40x40Name));
        Files.createDirectories(Paths.get(rootStorageDirectory, ImagesProcessor.directoryMosaicsName));
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
