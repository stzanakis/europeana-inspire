package eu.europeana;

import eu.europeana.accessors.BoardAccessor;
import eu.europeana.accessors.MeAccessor;
import eu.europeana.common.AccessorsManager;
import eu.europeana.common.Manager;
import eu.europeana.common.SaveImageFromUrl;
import eu.europeana.common.Tools;
import eu.europeana.exceptions.BadRequest;
import eu.europeana.exceptions.DoesNotExistException;
import eu.europeana.model.Pin;
import eu.europeana.model.PinsData;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.PropertiesConfigurationLayout;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.ConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @author Simon Tzanakis (Simon.Tzanakis@europeana.eu)
 * @since 2016-06-21
 */
public class Main {
    private static final Logger logger = LogManager.getLogger();
    private static final String directory100x100 = "100x100-size";
    private static final String directory60x60 = "60x60-size";
    private static final String directory40x40 = "40x40-size";
    public static void main(String[] args) throws IOException, ConfigurationException, org.apache.commons.configuration.ConfigurationException, BadRequest, DoesNotExistException, URISyntaxException, InterruptedException {
        logger.info("Started in Main");

        //INITIALIZE START
        String pinterestConfDirectory = "/data/credentials/pinterest-client";
        String europeanaInspireConfDirectory = "/data/credentials/europeana-inspire";
        String targetUser = "europeana";
        String targetBoard = "Heroes";

        //Load Pinterest Configuration Start
        AccessorsManager am = new AccessorsManager(pinterestConfDirectory);
        PropertiesConfiguration propertiesConfigurationPinterest = new PropertiesConfiguration();
        PropertiesConfigurationLayout configurationPropertiesLayoutPinterest = new PropertiesConfigurationLayout(propertiesConfigurationPinterest);
        File configurationFilePinterest = new File(am.getDefaultPropertiesPath() + "/" + AccessorsManager.getConfigurationFileName());
        if(configurationFilePinterest.exists())
            configurationPropertiesLayoutPinterest.load(new FileReader(configurationFilePinterest));
        else
            configurationPropertiesLayoutPinterest.load(new FileReader(Main.class.getClassLoader().getResource(AccessorsManager.getConfigurationFileName()).getFile()));
        //Load Pinterest Configuration End

        //Load Europeana Inspire Start
        Manager manager = new Manager(europeanaInspireConfDirectory);
        PropertiesConfiguration propertiesConfigurationInspire = new PropertiesConfiguration();
        PropertiesConfigurationLayout configurationPropertiesLayoutInspire = new PropertiesConfigurationLayout(propertiesConfigurationInspire);
        File configurationFileInspire = new File(Manager.getDefaultPropertiesPath() + "/" + Manager.getConfigurationFileName());
        if(configurationFileInspire.exists())
            configurationPropertiesLayoutInspire.load(new FileReader(configurationFileInspire));
        else
            configurationPropertiesLayoutInspire.load(new FileReader(Main.class.getClassLoader().getResource(Manager.getConfigurationFileName()).getFile()));
        //Load Europeana Inspire End

        am.initializeAllAccessors(propertiesConfigurationPinterest.getProperty(AccessorsManager.getAccessUrl_key()).toString());
        MeAccessor meAccessor = am.getMeAccessor();
        BoardAccessor boardAccessor = am.getBoardAccessor();
        //INITIALIZE END


        //PLAYGROUND START

//        System.out.println(boardAccessor.getBoardInformation("europeana", "Heroes"));
//        System.out.println(boardAccessor.getPinsFromBoard("simontzanakis", "Places"));
//        System.out.println(boardAccessor.getPinsFromBoard(targetUser, targetBoard));


//        System.out.println(meAccessor.getAllMyBoardsInternalName());


        //Get and store all pins from a specific board Start
        PinsData pinsFromBoard = boardAccessor.getPinsFromBoard(targetUser, targetBoard);
        SaveImageFromUrl saveImageFromUrl = new SaveImageFromUrl(propertiesConfigurationInspire.getProperty(Manager.getStorageDirectoryKey()).toString());

        String underDirectory = Tools.retrieveLastPathFromUrl(pinsFromBoard.getPins()[0].getBoard().getUrl());

        for (Pin pin:
        pinsFromBoard.getPins()) {
//            System.out.println(pin.getImage().getImage().getUrl());
//            System.out.println(pin.getImage().getImage().getWidth());
//            System.out.println(pin.getImage().getImage().getHeight());

            File file = saveImageFromUrl.saveImage(underDirectory, pin.getImage().getImage().getUrl());
            System.out.println("Saved at: " + file);
        }
        //Get and store all pins from a specific board End


        //Generate Mosaic
        String tilesDirectory= "/tmp/test/tiles-heroes-resize";
        String inputImage = "/tmp/test/europeana.png";
        String outputImage = "/tmp/test/output.png";

//        MosaicGenerator mosaicGenerator = new MosaicGenerator(tilesDirectory, inputImage, outputImage);
//        mosaicGenerator.generateMosaic();



        //Resizing images
        String testImage = "/tmp/heroes-test.jpg";
        String testImageResize = "/tmp/heroes-test-resize.jpg";
//        File folder = new File("/Users/me/Desktop/images/");
//        Thumbnails.of(folder.listFiles())
//                .size(112, 75)
//                .outputFormat("jpg").
//                .toFiles(Rename.PREFIX_DOT_THUMBNAIL);

//        BufferedImage thumbnail =
//                Thumbnails.of(new File(testImage))
//                        .forceSize(100, 100)
//                        .antialiasing(Antialiasing.ON)
//                        .outputQuality(1.0f)
//                        .asBufferedImage();
//
//        ImageIO.write(thumbnail, "jpg", new File(testImageResize));


        //PLAYGROUND END


        //CLOSE START
        am.closeAllAccessors();
        //CLOSE END

        logger.info("Ended in Main");
    }

}