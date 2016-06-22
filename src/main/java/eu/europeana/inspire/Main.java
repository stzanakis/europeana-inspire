package eu.europeana.inspire;

import eu.europeana.accessors.BoardAccessor;
import eu.europeana.accessors.MeAccessor;
import eu.europeana.common.AccessorsManager;
import eu.europeana.common.Manager;
import eu.europeana.exceptions.BadRequest;
import eu.europeana.exceptions.DoesNotExistException;
import eu.europeana.inspire.common.MosaicGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.ConfigurationException;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @author Simon Tzanakis (Simon.Tzanakis@europeana.eu)
 * @since 2016-06-21
 */
public class Main {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) throws IOException, ConfigurationException, org.apache.commons.configuration.ConfigurationException, BadRequest, DoesNotExistException, URISyntaxException, InterruptedException {
        logger.info("Started in Main");

        //INITIALIZE START
        String pinterestConfDirectory = "/data/credentials/pinterest-client";
        String europeanaInspireConfDirectory = "/data/credentials/europeana-inspire";
        String targetUser = "europeana";
        String targetBoard = "Heroes";

        //Load Pinterest Configuration Start
        AccessorsManager am = new AccessorsManager(pinterestConfDirectory);
        //Load Pinterest Configuration End

        //Load Europeana Inspire Start
        Manager manager = new Manager(europeanaInspireConfDirectory);
        //Load Europeana Inspire End

        am.initializeAllAccessors();
        MeAccessor meAccessor = am.getMeAccessor();
        BoardAccessor boardAccessor = am.getBoardAccessor();
        //INITIALIZE END


        //PLAYGROUND START

//        TestTerminal.executeCommands();

//        ProcessBuilder pb = new ProcessBuilder("ls");
//        pb.inheritIO();
//        pb.directory(new File("/tmp"));
//        pb.redirectOutput(ProcessBuilder.Redirect.appendTo(new File("/tmp/test.txt")));
//        pb.start();

//        System.out.println(boardAccessor.getBoardInformation("europeana", "Heroes"));
//        System.out.println(boardAccessor.getPinsFromBoard("simontzanakis", "Places"));
//        System.out.println(meAccessor.getAllMyBoardsInternalName());


        //Get and store all pins from a specific board Start
//        PinsData pinsFromBoard = boardAccessor.getPinsFromBoard(targetUser, targetBoard);
//        ImagesProcessor.storeAllPins(manager.getRootStorageDirectory(), pinsFromBoard);

//        List<String> allMyBoardsInternalName = meAccessor.getAllMyBoardsInternalName();
//        PinsData pinsFromBoard = boardAccessor.getAllPinsFromBoard(targetUser, allMyBoardsInternalName.get(1));
//        ImagesProcessor.storeAllPins(manager.getRootStorageDirectory(), pinsFromBoard);

//        for (String boardName :
//                allMyBoardsInternalName) {
//            PinsData pinsFromBoard = boardAccessor.getAllPinsFromBoard(targetUser, boardName);
//            ImagesProcessor.storeAllPins(manager.getRootStorageDirectory(), pinsFromBoard);
//
//        }
        //Generate until you fail...
        String inputImage = "/tmp/test/input2.jpg";
        String outputImage = "/tmp/test/output.png";

//        use100x100(inputImage, outputImage);
//        use60x60(inputImage, outputImage);
//        use40x40(inputImage, outputImage);
        //Get and store all pins from a specific board End


        //Generate Mosaic
//        String tilesDirectory = "/tmp/test/tiles-heroes-resize";
//        int size = 40;
//        int scale = 10;
//        String tilesDirectory = "/tmp/europeana-inspire/100x100-size/heroes";
//        String inputImage = "/tmp/test/input2.jpg";
//        String outputImage = "/tmp/test/output.png";
//
//        MosaicGenerator mosaicGenerator = new MosaicGenerator(tilesDirectory, inputImage, outputImage, size, size, scale);
//        mosaicGenerator.generateMosaic();

//        String inputImage = "/tmp/test/input2.jpg";
//        String outputImage = "/tmp/test/output.png";
//
////        use100x100(inputImage, outputImage);
////        use60x60(inputImage, outputImage);
//        use40x40(inputImage, outputImage);
        //PLAYGROUND END


        //CLOSE START
        am.closeAllAccessors();
        //CLOSE END

        logger.info("Ended in Main");
    }

    private static void use100x100(String inputImage, String outputImage) throws IOException, InterruptedException {
        int scale = 4;
        int size = 100;
        String tilesDirectory = "/tmp/europeana-inspire/100x100-size/heroes";
        logger.info("100x100 processing..");
        MosaicGenerator mosaicGenerator = new MosaicGenerator(tilesDirectory, inputImage, outputImage, size, size, scale);
        mosaicGenerator.generateMosaic();
    }

    private static void use60x60(String inputImage, String outputImage) throws IOException, InterruptedException {
        int scale = 8;
        int size = 60;
        String tilesDirectory = "/tmp/europeana-inspire/60x60-size";
        logger.info("60x60 processing..");
        MosaicGenerator mosaicGenerator = new MosaicGenerator(tilesDirectory, inputImage, outputImage, size, size, scale);
        mosaicGenerator.generateMosaic();
    }

    private static void use40x40(String inputImage, String outputImage) throws IOException, InterruptedException {
        int scale = 10;
        int size = 40;
        String tilesDirectory = "/tmp/europeana-inspire/40x40-size";
        logger.info("40x40 processing..");
        MosaicGenerator mosaicGenerator = new MosaicGenerator(tilesDirectory, inputImage, outputImage, size, size, scale);
        mosaicGenerator.generateMosaic();
    }


}