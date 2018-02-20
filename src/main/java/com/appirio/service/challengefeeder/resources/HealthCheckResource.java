/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.resources;

import com.appirio.service.supply.resources.MetadataApiResponseFactory;
import com.appirio.tech.core.api.v3.request.annotation.AllowAnonymous;
import com.appirio.tech.core.api.v3.response.ApiResponse;
import com.codahale.metrics.annotation.Timed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Resource to handle the challenge feeder
 * 
 * 
 * @author TCSCODER
 * @version 1.0
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("esfeeder")
public class HealthCheckResource {
    /**
     * Create HealthCheckResource
     */
    public HealthCheckResource() {
    }
    
    /**
     * Checks the service health
     *
     * @return the api response
     */
    @GET
    @Path("/healthcheck")
    @AllowAnonymous
    @Timed
    public ApiResponse checkHealth() {
        return MetadataApiResponseFactory.createResponse(null);
    }
}
