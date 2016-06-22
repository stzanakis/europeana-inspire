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

//        System.out.println(boardAccessor.getBoardInformation("europeana", "Heroes"));
//        System.out.println(boardAccessor.getPinsFromBoard("simontzanakis", "Places"));
//        System.out.println(meAccessor.getAllMyBoardsInternalName());


        //Get and store all pins from a specific board Start
//        PinsData pinsFromBoard = boardAccessor.getPinsFromBoard(targetUser, targetBoard);
//        ImagesProcessor.storeAllPins(manager.getRootStorageDirectory(), pinsFromBoard);

//        List<String> allMyBoardsInternalName = meAccessor.getAllMyBoardsInternalName();
//        PinsData pinsFromBoard = boardAccessor.getAllPinsFromBoard(targetUser, allMyBoardsInternalName.get(0));
//        ImagesProcessor.storeAllPins(manager.getRootStorageDirectory(), pinsFromBoard);
        //Get and store all pins from a specific board End


        //Generate Mosaic
//        String tilesDirectory = "/tmp/test/tiles-heroes-resize";
        String tilesDirectory = "/tmp/europeana-inspire/100x100-size/heroes";
        String inputImage = "/tmp/test/europeana.png";
        String outputImage = "/tmp/test/output.png";

        MosaicGenerator mosaicGenerator = new MosaicGenerator(tilesDirectory, inputImage, outputImage, 100, 100, 14);
        mosaicGenerator.generateMosaic();
        //PLAYGROUND END


        //CLOSE START
        am.closeAllAccessors();
        //CLOSE END

        logger.info("Ended in Main");
    }



}