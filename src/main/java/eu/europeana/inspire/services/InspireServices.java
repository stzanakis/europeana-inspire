package eu.europeana.inspire.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.europeana.accessors.BoardAccessor;
import eu.europeana.accessors.MeAccessor;
import eu.europeana.common.Manager;
import eu.europeana.common.Tools;
import eu.europeana.exceptions.BadRequest;
import eu.europeana.exceptions.DoesNotExistException;
import eu.europeana.inspire.common.ImagesProcessor;
import eu.europeana.inspire.common.MosaicGeneratorBash;
import eu.europeana.inspire.model.Result;
import eu.europeana.model.PinsData;
import eu.europeana.rest.configuration.ServletContext;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Tzanakis (Simon.Tzanakis@europeana.eu)
 * @since 2016-06-23
 */
@Path("/")
public class InspireServices {
    private static final Logger logger = LogManager.getLogger();
    @GET
    @Path("boards/{user}")
    @Produces("application/json")
    public String getAllBoardsFromUser(@PathParam("user") String targetUser) throws IOException, ConfigurationException, DoesNotExistException, URISyntaxException, BadRequest {
        MeAccessor meAccessor = Manager.accessorsManager.getMeAccessor();
        List<String> allMyBoardsInternalName = meAccessor.getAllMyBoardsInternalName(targetUser);

        JSONObject obj = new JSONObject();
        obj.put("board-count", new Integer(allMyBoardsInternalName.size()));
        obj.put("names", allMyBoardsInternalName);

        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(obj.toString()).getAsJsonObject();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(json);

        return prettyJson;
    }

    @POST
    @Path("mosaic/{user}/{board}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces("application/json")
    public Response createMosaicFromBoard(@PathParam("board") String targetBoard,
                                          @QueryParam("scale") int scale,
                                          @QueryParam("size") int size,
                                          @DefaultValue("true") @QueryParam("updates") boolean updates,
                                          @FormDataParam("file") InputStream uploadedInputStream,
                                          @FormDataParam("fileName") String fileName) throws IOException, DoesNotExistException, URISyntaxException, BadRequest {

        File tempFile = Paths.get(ServletContext.manager.getRootStorageDirectory(), fileName).toFile();
        // save temp file
        writeToFile(uploadedInputStream, tempFile.toString());
        Result result = getImagesAndgenerateMyMosaic(updates, scale, size, targetBoard, tempFile.toString());
        String linkToMosaic = result.getLink();
        if(linkToMosaic == null)
            return Response.status(400).entity("Bad request").build();

        InetAddress ip = InetAddress.getLocalHost();
        String hostname = ip.getHostName();

        tempFile.delete();
        logger.info("Get your image here fs: " + linkToMosaic + "\n");
        String parent = Tools.retrieveLastPathFromUrl(ServletContext.manager.getRootStorageDirectory());
        String substring = linkToMosaic.substring(linkToMosaic.indexOf(parent));
        String link = new URL(ServletContext.hardcodedUrl + "/" + substring).toString();
        result.setLink(link); //Update link to actual http link

        return Response.status(200).entity(result).build();
    }

    @GET
    @Path("mosaic/{director}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces("application/json")
    public Response getDirector(@PathParam("director") String director) throws URISyntaxException, IOException, BadRequest, DoesNotExistException {
        String sourceImage = null;
        switch (director)
        {
            case "david":
                sourceImage = "/data/datastore/europeana-inspire/directors/David.jpg";
                break;
            case "albert":
                sourceImage = "/data/datastore/europeana-inspire/directors/Albert.jpg";
                break;
            case "harry":
                sourceImage = "/data/datastore/europeana-inspire/directors/Harry.jpg";
                break;
            case "jill":
                sourceImage = "/data/datastore/europeana-inspire/directors/Jill.jpg";
                break;
        }
        Result result = getImagesAndgenerateMyMosaic(false, 20, 100, "all-boards", sourceImage);
        String linkToMosaic = result.getLink();
        if(linkToMosaic == null)
            return Response.status(400).entity("Bad request").build();

        InetAddress ip = InetAddress.getLocalHost();
        String hostname = ip.getHostName();

        logger.info("Get your image here fs: " + linkToMosaic + "\n");
        String parent = Tools.retrieveLastPathFromUrl(ServletContext.manager.getRootStorageDirectory());
        String substring = linkToMosaic.substring(linkToMosaic.indexOf(parent));
        String link = new URL(ServletContext.hardcodedUrl + "/" + substring).toString();
        result.setLink(link); //Update link to actual http link

        return Response.status(200).entity(result).build();
    }

    public static Result getImagesAndgenerateMyMosaic(boolean withUpdates, int scale, int size, String boardName, String sourceImage) throws BadRequest, DoesNotExistException, IOException, URISyntaxException {
        String targetBoard = boardName;
        String scaleSubdirectory = null;
        if(scale > 20 ) {
            logger.error("Available scales: <= 10");
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
            case 30:
                scaleSubdirectory = ImagesProcessor.directory30x30Name;
                break;
            case 20:
                scaleSubdirectory = ImagesProcessor.directory20x20Name;
                break;
            default: {
                logger.error("Available sizes : 100, 60, 40, 30, 20");
                return null;
            }
        }

        BoardAccessor boardAccessor = Manager.accessorsManager.getBoardAccessor();
        //Download images from pinterest
        //For all access we need to invoke the calls daily to retrieve
        if(!targetBoard.equals("all-boards") && withUpdates == true) {
            PinsData pinsFromBoard = boardAccessor.getPinsFromBoard(ServletContext.targetUser, targetBoard);
            ImagesProcessor.storeAllPins(ServletContext.manager.getRootStorageDirectory(), pinsFromBoard);
        }

        //Generate mosaic
        String outputFileName = scale + "-" + size + "x" + size + "-" + Tools.retrieveLastPathFromUrl(sourceImage);
        ArrayList<String> sourceTilesDirectories = new ArrayList<>();
        if(!targetBoard.equals("all-boards"))
            sourceTilesDirectories.add(Paths.get(ServletContext.manager.getRootStorageDirectory(), scaleSubdirectory, boardName).toString());
        else {
            //Get list of all directories with relevant size
            String directory = Paths.get(ServletContext.manager.getRootStorageDirectory(), scaleSubdirectory).toString();
            File[] files = new File(directory).listFiles();
            for(File file : files){
                if(file.isDirectory()){
                    sourceTilesDirectories.add(file.getAbsolutePath());
                }
            }
        }

        java.nio.file.Path mosaicsDirectory = Paths.get(ServletContext.manager.getRootStorageDirectory(), ImagesProcessor.directoryMosaicsName);
        String output = Paths.get(mosaicsDirectory.toString(), outputFileName).toString();

        int uniqueTileCount = MosaicGeneratorBash.generateMosaic(scale, size, size, sourceTilesDirectories, sourceImage, output, (short) 10);
        Result result = new Result(uniqueTileCount, output);

        return result;
    }

        // save uploaded file to new location
    private void writeToFile(InputStream uploadedInputStream,
                             String uploadedFileLocation) {

        try {
            OutputStream out = new FileOutputStream(new File(
                    uploadedFileLocation));
            int read = 0;
            byte[] bytes = new byte[1024];

            out = new FileOutputStream(new File(uploadedFileLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

}
