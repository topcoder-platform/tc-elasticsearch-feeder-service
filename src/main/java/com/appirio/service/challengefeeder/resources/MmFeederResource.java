/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.resources;

import com.appirio.service.challengefeeder.dto.MmFeederParam;
import com.appirio.service.challengefeeder.manager.MmFeederManager;
import com.appirio.service.supply.resources.MetadataApiResponseFactory;
import com.appirio.supply.ErrorHandler;
import com.appirio.supply.SupplyException;
import com.appirio.tech.core.api.v3.request.PostPutRequest;
import com.appirio.tech.core.api.v3.response.ApiResponse;
import com.appirio.tech.core.auth.AuthUser;
import com.codahale.metrics.annotation.Timed;

import io.dropwizard.auth.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Resource to handle the marathon match feeder
 * 
 * It's added in Topcoder - Populate Marathon Match Related Data Into Challenge Model In Elasticsearch v1.0
 * 
 * 
 * @author TCSCODER
 * @version 1.0
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("elastic/mmatches")
public class MmFeederResource {

    /**
     * Logger used to log the events
     */
    private static final Logger logger = LoggerFactory.getLogger(MmFeederResource.class);

    /**
     * Manager to handle the marathon match feeders
     */
    private final MmFeederManager mmFeederManager;

    /**
     * Create MmFeederResource
     *
     * @param mmFeederManager the mmFeederManager to use
     */
    public MmFeederResource(MmFeederManager mmFeederManager) {
        this.mmFeederManager = mmFeederManager;
    }
    
    /**
     * Push marathon match data into the challenge model.
     *
     * @param user the user to use
     * @param request the request to use
     * @return the ApiResponse result
     */
    @PUT
    @Path("challenges")
    @Timed
    public ApiResponse pushMarathonMatchDataIntoChallenge(@Auth AuthUser user, @Valid PostPutRequest<MmFeederParam> request) {
        try {
            if (request == null || request.getParam() == null) {
                throw new SupplyException("The request body should be provided", HttpServletResponse.SC_BAD_REQUEST);
            }
            this.mmFeederManager.pushMarathonMatchDataIntoChallenge(user, request.getParam());
            return MetadataApiResponseFactory.createResponse(null);
        } catch (Exception e) {
            return ErrorHandler.handle(e, logger);
        }
    }
}
