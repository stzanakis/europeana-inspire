package eu.europeana.common;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Simon Tzanakis (Simon.Tzanakis@europeana.eu)
 * @since 2016-06-21
 */
public class SaveImageFromUrl {
    private static final Logger logger = LogManager.getLogger();
    private String defaultDirectoryOfSavedImages;
    private final String originalDir = "original-size";

    public SaveImageFromUrl(String defaultDirectoryOfSavedImages) {
        this.defaultDirectoryOfSavedImages = defaultDirectoryOfSavedImages;
    }

    public File saveImage(String underDirectory, String imageUrl, String destinationFile) throws IOException {
        String file = Paths.get(defaultDirectoryOfSavedImages, destinationFile).toString();
        return downLoadImage(imageUrl, file);
    }

    public File saveImage(String underDirectory, String imageUrl) throws IOException {
        String path = parsePathFromUrl(imageUrl);
        Files.createDirectories(Paths.get(defaultDirectoryOfSavedImages, originalDir, underDirectory, path));

        String fullName = FilenameUtils.getName(imageUrl);

        Path newFilePath = Paths.get(path, fullName);
        String file = Paths.get(defaultDirectoryOfSavedImages, originalDir, underDirectory, newFilePath.toString()).toString();
        File destinationFile = new File(file);
        if(destinationFile.exists()) {
            logger.info("Did not re-download, image already exists!");
            return destinationFile;
        }
        else {
            logger.info("Triggering download, image does not exist!");
            return downLoadImage(imageUrl, file);
        }
    }

    private File downLoadImage(String imageUrl, String destinationFile) throws IOException {
        URL url = new URL(imageUrl);
        InputStream is = url.openStream();
        File file = new File(destinationFile);
        OutputStream os = new FileOutputStream(file);

        byte[] b = new byte[2048];
        int length;

        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }

        is.close();
        os.close();

        return file;
    }


    public String parsePathFromUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        String directoryPath = url.getPath().substring(0, url.getPath().lastIndexOf("/"));

//        Path directories = Files.createDirectories(Paths.get(defaultDirectoryOfSavedImages, directoryPath));

        return directoryPath;
    }
}

