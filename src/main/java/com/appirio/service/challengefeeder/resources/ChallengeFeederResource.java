/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.resources;

import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appirio.service.challengefeeder.dto.ChallengeFeederParam;
import com.appirio.service.challengefeeder.manager.ChallengeDetailFeederManager;
import com.appirio.service.challengefeeder.manager.ChallengeListingFeederManager;
import com.appirio.service.supply.resources.MetadataApiResponseFactory;
import com.appirio.supply.ErrorHandler;
import com.appirio.supply.SupplyException;
import com.appirio.tech.core.api.v3.request.PostPutRequest;
import com.appirio.tech.core.api.v3.request.annotation.AllowAnonymous;
import com.appirio.tech.core.api.v3.response.ApiResponse;
import com.codahale.metrics.annotation.Timed;

/**
 * Resource to handle the challenge feeder
 * 
 * Version 1.1 - Topcoder Elasticsearch Feeder Service - Jobs Cleanup And Improvement v1.0
 * - make it dummy
 * 
 *
 * Version 1.2 - Implement Endpoint To Populate Elasticsearch For The Given Challenges
 * - Added method to call listing and detail feeder manager to populate challenge
 * 	 data into ES using API resource
 * 
 * @author TCSCODER
 * @version 1.2 
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
     * Manager to access  the challenge feeder for listing.
     */
    private final ChallengeListingFeederManager challengeListingFeederManager;
    
    /**
     * Manager to access  the challenge details feeder for listing.
     */
    private final ChallengeDetailFeederManager challengeDetailFeederManager;
    

    /**
     * Create ChallengeFeederResource
     */
    public ChallengeFeederResource(ChallengeDetailFeederManager challengeDetailFeederManager,ChallengeListingFeederManager challengeListingFeederManager) {
    	this.challengeDetailFeederManager = challengeDetailFeederManager;
    	this.challengeListingFeederManager = challengeListingFeederManager;
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
			if (request == null || request.getParam() == null) {
				throw new SupplyException("The request body should be provided", HttpServletResponse.SC_BAD_REQUEST);
			}

			
			challengeListingFeederManager.pushChallengeFeeder(request.getParam());
			challengeDetailFeederManager.pushChallengeFeeder(request.getParam());
			return MetadataApiResponseFactory.createResponse(null);
		} catch (Exception e) {
			return ErrorHandler.handle(e, logger);
		}
    }
}
