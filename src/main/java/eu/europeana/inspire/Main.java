package eu.europeana.inspire;

import eu.europeana.accessors.BoardAccessor;
import eu.europeana.accessors.MeAccessor;
import eu.europeana.common.Manager;
import eu.europeana.common.Tools;
import eu.europeana.exceptions.BadRequest;
import eu.europeana.exceptions.DoesNotExistException;
import eu.europeana.inspire.common.ImagesProcessor;
import eu.europeana.inspire.common.MosaicGeneratorBash;
import eu.europeana.model.PinsData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.ConfigurationException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Needs metapixel bash command installed on the host system.
 *
 * @author Simon Tzanakis (Simon.Tzanakis@europeana.eu)
 * @since 2016-06-21
 */
public class Main {
    private static final Logger logger = LogManager.getLogger();
    private static Manager manager;
    private static MeAccessor meAccessor;
    private static BoardAccessor boardAccessor;
    private static String targetUser = "europeana";

    public static void main(String[] args) throws IOException, ConfigurationException, org.apache.commons.configuration.ConfigurationException, BadRequest, DoesNotExistException, URISyntaxException, InterruptedException {
        logger.info("Started in Main");

        //INITIALIZE START
        String pinterestConfDirectory = "/data/credentials/pinterest-client";
        String europeanaInspireConfDirectory = "/data/credentials/europeana-inspire";

        //Load Europeana Inspire Start
        Manager manager = new Manager(europeanaInspireConfDirectory, pinterestConfDirectory);
        //Load Europeana Inspire End

        Manager.accessorsManager.initializeAllAccessors();
        meAccessor = Manager.accessorsManager.getMeAccessor();
        boardAccessor = Manager.accessorsManager.getBoardAccessor();
        //INITIALIZE END


        //PLAYGROUND START

//        MosaicGeneratorBash.prepareImages(100, 100, "/tmp/europeana-inspire/original-size/heroes", "/tmp/test/ml");

//        MosaicGeneratorBash.generateMosaic(4, 100, 100,  "/tmp/test/ml", "/tmp/test/input2.jpg", "/tmp/test/output.png", (short) 10);

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

//        MosaicGeneratorBash.generateMosaic(6, 100, 100, "/tmp/europeana-inspire/100x100-size/heroes", "/tmp/test/input2.jpg", "/tmp/test/output100.png", (short) 10);
//        MosaicGeneratorBash.generateMosaic(6, 60, 60, "/tmp/europeana-inspire/60x60-size/heroes", "/tmp/test/input2.jpg", "/tmp/test/output60.png", (short) 10);
//        MosaicGeneratorBash.generateMosaic(6, 40, 40, "/tmp/europeana-inspire/60x60-size/heroes", "/tmp/test/input2.jpg", "/tmp/test/output40.png", (short) 10);

        getImagesAndgenerateMyMosaic(4, 100, "heroes", "/tmp/test/input2.jpg");
//        generateMyMosaic(4, 60, "heroes", "/tmp/test/input2.jpg");

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
//        String inputImage = "/tmp/test/input2.jpg";
//        String outputImage = "/tmp/test/output.png";

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
        Manager.accessorsManager.closeAllAccessors();
        //CLOSE END

        logger.info("Ended in Main");
    }

    public static String getImagesAndgenerateMyMosaic(int scale, int size, String boardName, String sourceImage) throws BadRequest, DoesNotExistException, IOException, URISyntaxException {
        String targetBoard = boardName;
        String scaleSubdirectory = null;
        if(scale > 8 ) {
            logger.error("Available scales: <= 8");
            return null;
        }
        switch (size)
        {
            case 100:
                scaleSubdirectory = ImagesProcessor.directory100x100Name;
                break;
            case 60:
                scaleSubdirectory = ImagesProcessor.directory60x60Name;
                break;
            case 40:
                scaleSubdirectory = ImagesProcessor.directory40x40Name;
                break;
            default: {
                logger.error("Available sizes : 100, 60, 40");
                return null;
            }
        }

        //Download images from pinterest
        //For all access we need to invoke the calls daily to retrieve
        if(!targetBoard.equals("all-boards")) {
            PinsData pinsFromBoard = boardAccessor.getPinsFromBoard(targetUser, targetBoard);
            ImagesProcessor.storeAllPins(manager.getRootStorageDirectory(), pinsFromBoard);
        }

        //Generate mosaic
        String outputFileName = scale + "-" + size + "x" + size + "-" + Tools.retrieveLastPathFromUrl(sourceImage);
        String sourceTilesDirectory;
        if(!targetBoard.equals("all-boards"))
            sourceTilesDirectory = Paths.get(manager.getRootStorageDirectory(), scaleSubdirectory, boardName).toString();
        else
            sourceTilesDirectory = Paths.get(manager.getRootStorageDirectory(), scaleSubdirectory).toString();

        Path mosaicsDirectory = Paths.get(manager.getRootStorageDirectory(), ImagesProcessor.directoryMosaicsName);
        String output = Paths.get(mosaicsDirectory.toString(), outputFileName).toString();

        MosaicGeneratorBash.generateMosaic(scale, size, size, sourceTilesDirectory, sourceImage, output, (short) 10);

        return output;
    }

    private static void generateMyMosaic(int scale, int size, String boardName, String sourceImage) throws BadRequest, DoesNotExistException, IOException, URISyntaxException {
        String targetBoard = boardName;
        String scaleSubdirectory = null;
        if(scale > 8 ) {
            logger.error("Available scales: <= 8");
            return;
        }
        switch (size)
        {
            case 100:
                scaleSubdirectory = ImagesProcessor.directory100x100Name;
                break;
            case 60:
                scaleSubdirectory = ImagesProcessor.directory60x60Name;
                break;
            case 40:
                scaleSubdirectory = ImagesProcessor.directory40x40Name;
                break;
            default: {
                logger.error("Available sizes : 100, 60, 40");
                return;
            }
        }

        //Generate mosaic
        String outputFileName = scale + "-" + size + "x" + size + "-" + Tools.retrieveLastPathFromUrl(sourceImage);
        String sourceTilesDirectory;
        if(!targetBoard.equals("all-boards"))
            sourceTilesDirectory = Paths.get(manager.getRootStorageDirectory(), scaleSubdirectory, boardName).toString();
        else
            sourceTilesDirectory = Paths.get(manager.getRootStorageDirectory(), scaleSubdirectory).toString();

        Path mosaicsDirectory = Paths.get(manager.getRootStorageDirectory(), ImagesProcessor.directoryMosaicsName);
        String output = Paths.get(mosaicsDirectory.toString(), outputFileName).toString();

        MosaicGeneratorBash.generateMosaic(scale, size, size, sourceTilesDirectory, sourceImage, output, (short) 10);
    }

//    private static void use100x100(String inputImage, String outputImage) throws IOException, InterruptedException {
//        int scale = 4;
//        int size = 100;
//        String tilesDirectory = "/tmp/europeana-inspire/100x100-size/heroes";
//        logger.info("100x100 processing..");
//        MosaicGenerator mosaicGenerator = new MosaicGenerator(tilesDirectory, inputImage, outputImage, size, size, scale);
//        mosaicGenerator.generateMosaic();
//    }
//
//    private static void use60x60(String inputImage, String outputImage) throws IOException, InterruptedException {
//        int scale = 8;
//        int size = 60;
//        String tilesDirectory = "/tmp/europeana-inspire/60x60-size";
//        logger.info("60x60 processing..");
//        MosaicGenerator mosaicGenerator = new MosaicGenerator(tilesDirectory, inputImage, outputImage, size, size, scale);
//        mosaicGenerator.generateMosaic();
//    }
//
//    private static void use40x40(String inputImage, String outputImage) throws IOException, InterruptedException {
//        int scale = 10;
//        int size = 40;
//        String tilesDirectory = "/tmp/europeana-inspire/40x40-size";
//        logger.info("40x40 processing..");
//        MosaicGenerator mosaicGenerator = new MosaicGenerator(tilesDirectory, inputImage, outputImage, size, size, scale);
//        mosaicGenerator.generateMosaic();
//    }


}