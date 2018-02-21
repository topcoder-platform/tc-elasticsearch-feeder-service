/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.resources;

import com.appirio.service.challengefeeder.dto.DataScienceFeederParam;
import com.appirio.service.challengefeeder.manager.MarathonMatchFeederManager;
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
 * <p>
 * Added in Topcoder - Add Endpoints To Populating Marathon Matches And SRMs Into Elasticsearch v1.0
 * </p>
 *
 * @author TCCoder
 * @version 1.0
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("elastic/mmatches")
public class MarathonMatchFeederResource {

    /**
     * Logger used to log the events
     */
    private static final Logger logger = LoggerFactory.getLogger(MarathonMatchFeederResource.class);

    /**
     * Manager to access search business logic
     */
    private final MarathonMatchFeederManager marathonMatchFeederManager;

    /**
     * Create MarathonMatchFeederResource
     *
     * @param marathonMatchFeederManager
     *            the marathonMatchFeederManager to use
     */
    public MarathonMatchFeederResource(MarathonMatchFeederManager marathonMatchFeederManager) {
        this.marathonMatchFeederManager = marathonMatchFeederManager;
    }

    /**
     * Push marathon match feeders to elasticsearch service
     *
     * @param user
     *            the authenticated user
     * @param request
     *            the request
     * @return the api response
     */
    @PUT
    @Timed
    public ApiResponse pushMarathonMatchFeeders(@Auth AuthUser user,
        @Valid PostPutRequest<DataScienceFeederParam> request) {
        try {
            if (request == null || request.getParam() == null) {
                throw new SupplyException("The request body should be provided", HttpServletResponse.SC_BAD_REQUEST);
            }
            this.marathonMatchFeederManager.pushMarathonMatchFeeder(user, request.getParam());
            return MetadataApiResponseFactory.createResponse(null);
        } catch (Exception e) {
            return ErrorHandler.handle(e, logger);
        }
    }
}
