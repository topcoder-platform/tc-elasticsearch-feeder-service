/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.resources;

import com.appirio.service.challengefeeder.dto.ChallengeFeederParam;
import com.appirio.service.challengefeeder.manager.ChallengeFeederManager;
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
 * Resource to handle the challenge feeder
 * 
 * 
 * @author TCSCODER
 * @version 1.0
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("elastic/challenges")
public class ChallengeFeederResource {

    /**
     * Logger used to log the events
     */
    private static final Logger logger = LoggerFactory.getLogger(ChallengeFeederResource.class);

    /**
     * Manager to access search business logic
     */
    private final ChallengeFeederManager challengeFeederManager;

    /**
     * Create ChallengeFeederResource
     *
     * @param challengeFeederManager the challengeManager to use
     */
    public ChallengeFeederResource(ChallengeFeederManager challengeFeederManager) {
        this.challengeFeederManager = challengeFeederManager;
    }
    
    /**
     * Push challenge feeders to elasticsearch service
     * 
     * @param user the authenticated user
     * @param request the request
     * @return the api response
     */
    @PUT
    @Timed
    public ApiResponse pushChallengeFeeders(@Auth AuthUser user, @Valid PostPutRequest<ChallengeFeederParam> request) {
        try {
            if (request == null || request.getParam() == null) {
                throw new SupplyException("The request body should be provided", HttpServletResponse.SC_BAD_REQUEST);
            }
            this.challengeFeederManager.pushChallengeFeeder(user, request.getParam());
            return MetadataApiResponseFactory.createResponse(null);
        } catch (Exception e) {
            return ErrorHandler.handle(e, logger);
        }
    }
}
