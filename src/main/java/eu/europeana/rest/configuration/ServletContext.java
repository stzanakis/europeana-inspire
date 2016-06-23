package eu.europeana.rest.configuration;

import eu.europeana.common.AccessorsManager;
import eu.europeana.common.Manager;
import org.apache.commons.configuration.ConfigurationException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Simon Tzanakis (Simon.Tzanakis@europeana.eu)
 * @since 2016-06-23
 */
public class ServletContext implements ServletContextListener
{
    public void contextInitialized(ServletContextEvent arg0)
    {
        String pinterestConfDirectory = "/data/credentials/pinterest-client";
        String europeanaInspireConfDirectory = "/data/credentials/europeana-inspire";
        //Load Pinterest Configuration Start
        try {
            Manager.accessorsManager = new AccessorsManager(pinterestConfDirectory);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        //Load Pinterest Configuration End

        //Load Europeana Inspire Start
        try {
            Manager manager = new Manager(europeanaInspireConfDirectory, pinterestConfDirectory);
            Manager.accessorsManager.initializeAllAccessors();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        //Load Europeana Inspire End
    }//end contextInitialized method


    public void contextDestroyed(ServletContextEvent arg0)
    {
        Manager.accessorsManager.closeAllAccessors();
    }//end constextDestroyed method

}
