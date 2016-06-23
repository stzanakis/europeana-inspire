package eu.europeana.inspire.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.europeana.accessors.MeAccessor;
import eu.europeana.common.Manager;
import eu.europeana.exceptions.BadRequest;
import eu.europeana.exceptions.DoesNotExistException;
import org.apache.commons.configuration.ConfigurationException;
import org.json.simple.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @author Simon Tzanakis (Simon.Tzanakis@europeana.eu)
 * @since 2016-06-23
 */
@Path("/")
public class InspireServices {

    @GET
    @Produces("application/xml")
    public String convertCtoF() {

        Double fahrenheit;
        Double celsius = 36.8;
        fahrenheit = ((celsius * 9) / 5) + 32;

        String result = "@Produces(\"application/xml\") Output: \n\nC to F Converter Output: \n\n" + fahrenheit;
        return "<ctofservice>" + "<celsius>" + celsius + "</celsius>" + "<ctofoutput>" + result + "</ctofoutput>" + "</ctofservice>";

    }

    @GET
    @Path("boards/{user}")
    @Produces("application/json")
    public String getAllBoardsFromUser(@PathParam("user") String targetUser) throws IOException, ConfigurationException, DoesNotExistException, URISyntaxException, BadRequest {
        MeAccessor meAccessor = Manager.accessorsManager.getMeAccessor();
        List<String> allMyBoardsInternalName = meAccessor.getAllMyBoardsInternalName();

        JSONObject obj = new JSONObject();
        obj.put("table-count", new Integer(allMyBoardsInternalName.size()));
        obj.put("names", allMyBoardsInternalName);

        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(obj.toString()).getAsJsonObject();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(json);

        return prettyJson;
    }

}
