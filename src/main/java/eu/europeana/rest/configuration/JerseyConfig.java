/* JerseyConfig.java - created on Oct 14, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package eu.europeana.rest.configuration;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.MalformedURLException;

/**
 * Register all the resources for the jersey configuration
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 14, 2014
 */
public class JerseyConfig extends ResourceConfig {

    /**
     * Creates a new instance of this class.
     * @throws MalformedURLException 
     */
    public JerseyConfig() throws MalformedURLException {
        packages("eu.europeana.inspire.services");
        
        //Exceptions
//        register(DoesNotExistExceptionMapper.class);
//        register(AlreadyExistsExceptionMapper.class);
//        register(MissingArgumentsExceptionMapper.class);
//        register(InvalidArgumentsExceptionMapper.class);
//        register(InternalServerErrorExceptionMapper.class);
        
        //Features
        packages("org.glassfish.jersey.examples.multipart");
        register(MultiPartFeature.class);
    }

}
