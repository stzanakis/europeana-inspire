package eu.europeana.inspire.common;

import eu.europeana.common.Tools;
import eu.europeana.model.Pin;
import eu.europeana.model.PinsData;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.resizers.configurations.Antialiasing;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Simon Tzanakis (Simon.Tzanakis@europeana.eu)
 * @since 2016-06-22
 */
public class ImagesProcessor {
    private static final Logger logger = LogManager.getLogger();
    public static final String directoryOriginalSizeName = "original-size";
    public static final String directory100x100Name = "100x100-size";
    public static final String directory60x60Name = "60x60-size";
    public static final String directory40x40Name = "40x40-size";
    public static final String directoryMosaicsName = "mosaics";

    public static void storeAllPins(String rootStorageDirectory, PinsData pinsFromBoard) throws URISyntaxException, IOException {
        SaveImageFromUrl saveImageFromUrl = new SaveImageFromUrl(rootStorageDirectory);

        String underDirectory = Tools.retrieveLastPathFromUrl(pinsFromBoard.getPins().get(0).getBoard().getUrl());

        for (Pin pin :
                pinsFromBoard.getPins()) {
//            System.out.println(pin.getImage().getImage().getUrl());
//            System.out.println(pin.getImage().getImage().getWidth());
//            System.out.println(pin.getImage().getImage().getHeight());

            File file = saveImageFromUrl.saveImage(underDirectory, pin.getImage().getImage().getUrl());
            logger.info("Saved at: " + file);
            //Create required directories
//            String subTreeFile = file.toString().substring(Paths.get(rootStorageDirectory, "original-size").toString().length() + 1);
//            String subTreeDirectory = Paths.get(subTreeFile).getParent().toString();
//            Path path100x100 = Paths.get(rootStorageDirectory, directory100x100Name);
//            Files.createDirectories(path100x100);
//            Path path60x60 = Paths.get(rootStorageDirectory, directory60x60Name);
//            Files.createDirectories(path60x60);
//            Path path40x40 = Paths.get(rootStorageDirectory, directory40x40Name);
//            Files.createDirectories(path40x40);
//
//            //Resize each image when downloading, do not resize if existent
//            Files.createDirectories(Paths.get(path100x100.toString(), subTreeDirectory));
//            resizeImage(100, 100, file.toString(), Paths.get(path100x100.toString(), subTreeFile).toString(), "jpg");
//            Files.createDirectories(Paths.get(path60x60.toString(), subTreeDirectory));
//            resizeImage(60, 60, file.toString(), Paths.get(path60x60.toString(), subTreeFile).toString(), "jpg");
//            Files.createDirectories(Paths.get(path40x40.toString(), subTreeDirectory));
//            resizeImage(40, 40, file.toString(), Paths.get(path40x40.toString(), subTreeFile).toString(), "jpg");
        }

        String originalImages = Paths.get(rootStorageDirectory, "original-size", underDirectory).toString();
        resizeImages(rootStorageDirectory, originalImages, underDirectory);
    }

    private static void resizeImages(String rootStorageDirectory, String sourceDirectory, String boardName) throws IOException {
        Path path100x100 = Paths.get(rootStorageDirectory, directory100x100Name, boardName);
//        Files.createDirectories(path100x100);
        Path path60x60 = Paths.get(rootStorageDirectory, directory60x60Name, boardName);
//        Files.createDirectories(path60x60);
        Path path40x40 = Paths.get(rootStorageDirectory, directory40x40Name, boardName);
//        Files.createDirectories(path40x40);

        MosaicGeneratorBash.prepareImages(100, 100, sourceDirectory, path100x100.toString());
        MosaicGeneratorBash.prepareImages(60, 60, sourceDirectory, path60x60.toString());
        MosaicGeneratorBash.prepareImages(40, 40, sourceDirectory, path40x40.toString());
    }

    private static void resizeImage(int width, int height, String srcImage, String destImage, String destinationImageType) throws IOException {
        File destinationFile = new File(destImage);
        if (destinationFile.exists()) {
            logger.info("Did not re-do resizing to: " + width + "x" + height + ", image already exists!");
        } else {
            logger.info("Triggering re-size to : " + width + "x" + height + ", image does not exist!");
            BufferedImage thumbnail =
                    Thumbnails.of(new File(srcImage))
                            .forceSize(width, height)
                            .antialiasing(Antialiasing.ON)
                            .outputQuality(1.0f)
                            .asBufferedImage();

            ImageIO.write(thumbnail, destinationImageType, new File(destImage));
        }
    }
}
