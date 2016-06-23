package eu.europeana.inspire.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Tzanakis (Simon.Tzanakis@europeana.eu)
 * @since 2016-06-22
 */
public class MosaicGeneratorBash {
    private static final Logger logger = LogManager.getLogger();

    public static void generateMosaic(int scale, int width, int height, ArrayList<String> imageLibrary, String inputImage, String outputImage){
        StringBuilder command = new StringBuilder("metapixel --metapixel --scale=" + scale + " --distance=0 --width=" + width + " --height=" + height);
        for (String library :
                imageLibrary) {
            command.append(" --library=" + library);
        }

        command.append(" " + inputImage + " " + outputImage);
        logger.info("Starting command: " + command);
        runBashCommand(command.toString());
    }

    public static void generateMosaic(int scale, int width, int height, ArrayList<String> imageLibrary, String inputImage, String outputImage, short cheatValue){
        StringBuilder command = new StringBuilder("metapixel --metapixel --cheat=" + cheatValue + " --scale=" + scale + " --distance=0 --width=" + width + " --height=" + height);
        for (String library :
                imageLibrary) {
            command.append(" --library=" + library);
        }

        command.append(" " + inputImage + " " + outputImage);
        logger.info("Starting command: " + command);
        runBashCommand(command.toString());
    }

    public static void prepareImages(int width, int height, String sourceImagesDirectory, String convertedImagesDirectory) throws IOException {
        Files.createDirectories(Paths.get(convertedImagesDirectory));
        int totalFilesAlreadyConverted = 0;
        if(new File(convertedImagesDirectory).list() != null)
            totalFilesAlreadyConverted = new File(convertedImagesDirectory).list().length - 1; //-1 to remove the tables file
        int totalFilesFromSource = getFileNames(new ArrayList<>(), Paths.get(sourceImagesDirectory)).size();

        if(totalFilesAlreadyConverted == totalFilesFromSource)
        {
            logger.warn("Will not generate(prepare) files again, same number of files " +
                    "exists on destination. sourceNumber: " + totalFilesFromSource + ", destinationNumber: " + totalFilesAlreadyConverted);
            return;
        }

        String command = "metapixel-prepare --recurse --width=" + width + " --height=" + height + " " + sourceImagesDirectory + " " + convertedImagesDirectory;
        logger.info("Starting command: " + command);
        runBashCommand(command);
    }

    private static void runBashCommand(String command)
    {
        String s = null;

        try {
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));

            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
                logger.info(s);
            }

            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                logger.error(s);
            }
        }
        catch (IOException e) {
            logger.fatal("exception happened - here's what I know: ");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static List<String> getFileNames(List<String> fileNames, Path dir) {
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path path : stream) {
                if(path.toFile().isDirectory()) {
                    getFileNames(fileNames, path);
                } else {
                    fileNames.add(path.toAbsolutePath().toString());
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return fileNames;
    }
}
