/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.resources;

import com.appirio.service.challengefeeder.dto.ChallengeFeederParam;
import com.appirio.service.supply.resources.MetadataApiResponseFactory;
import com.appirio.supply.ErrorHandler;
import com.appirio.tech.core.api.v3.request.PostPutRequest;
import com.appirio.tech.core.api.v3.request.annotation.AllowAnonymous;
import com.appirio.tech.core.api.v3.response.ApiResponse;
import com.codahale.metrics.annotation.Timed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Resource to handle the challenge feeder
 * 
 * Version 1.1 - Topcoder Elasticsearch Feeder Service - Jobs Cleanup And Improvement v1.0
 * - make it dummy
 * 
 * 
 * 
 * @author TCSCODER
 * @version 1.1 
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("esfeeder/challenges")
public class ChallengeFeederResource {

    /**
     * Logger used to log the events
     */
    private static final Logger logger = LoggerFactory.getLogger(ChallengeFeederResource.class);

    /**
     * Create ChallengeFeederResource
     */
    public ChallengeFeederResource() {
    }
    
    /**
     * Push challenge feeders to elasticsearch service
     *
     * @param request the request
     * @return the api response
     */
    @PUT
    @Timed
    @AllowAnonymous
    public ApiResponse pushChallengeFeeders(@Valid PostPutRequest<ChallengeFeederParam> request) {
        try {
            return MetadataApiResponseFactory.createResponse(null);
        } catch (Exception e) {
            return ErrorHandler.handle(e, logger);
        }
    }
}
