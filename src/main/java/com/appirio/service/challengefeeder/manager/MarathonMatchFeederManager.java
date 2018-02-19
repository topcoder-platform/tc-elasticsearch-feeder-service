/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.manager;

import com.appirio.service.challengefeeder.Helper;
import com.appirio.service.challengefeeder.api.MarathonMatchData;
import com.appirio.service.challengefeeder.dao.MarathonMatchFeederDAO;
import com.appirio.service.challengefeeder.dto.DataScienceFeederParam;
import com.appirio.supply.SupplyException;
import com.appirio.tech.core.api.v3.request.QueryParameter;
import com.appirio.tech.core.auth.AuthUser;

import io.searchbox.client.JestClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;


/**
 * MarathonMatchFeederManager is used to handle the marathon match feeder.
 * <p>
 * Added in Topcoder - Add Endpoints To Populating Marathon Matches And SRMs Into Elasticsearch v1.0
 * </p>
 *
 * @author TCSCODER
 * @version 1.0
 */
public class MarathonMatchFeederManager {

    /**
     * Logger used to log events
     */
    private static final Logger logger = LoggerFactory.getLogger(MarathonMatchFeederManager.class);

    /**
     * DAO to access marathon match data from the database.
     */
    private final MarathonMatchFeederDAO marathonMatchFeederDAO;

    /**
     * The jestClient field
     */
    private final JestClient jestClient;

    /**
     * Create MarathonMatchFeederManager
     *
     * @param jestClient
     *            the jestClient to use
     * @param marathonMatchFeederDAO
     *            the marathonMatchFeederDAO to use
     */
    public MarathonMatchFeederManager(JestClient jestClient, MarathonMatchFeederDAO marathonMatchFeederDAO) {
        this.jestClient = jestClient;
        this.marathonMatchFeederDAO = marathonMatchFeederDAO;
    }

    /**
     * Push marathon match feeder
     *
     * @param authUser
     *            the authUser to use
     * @param param
     *            the data science feeders param to use
     * @throws SupplyException
     *             if any error occurs
     */
    public void pushMarathonMatchFeeder(AuthUser authUser, DataScienceFeederParam param) throws SupplyException {
        logger.info("Enter of pushMarathonMatchFeeder");
        Helper.checkAdmin(authUser);
        DataScienceHelper.checkDataScienceFeederParam(param, "mmatches");

        List<Long> roundIds = param.getRoundIds();
        // build query string to filter on round ids
        QueryParameter queryParameter = DataScienceHelper.buildInQueryParameter(roundIds, "roundIds");

        // fetch marathon matches data from persistence
        List<MarathonMatchData> marathonMatches = this.marathonMatchFeederDAO.getMarathonMatches(queryParameter);

        // check if all MMs with given roundIds exist
        DataScienceHelper.checkDataScienceExist(roundIds, marathonMatches);

        logger.info("Total hits:" + marathonMatches.size());

        List<Map<String, Object>> userIds = this.marathonMatchFeederDAO.getUserIds(queryParameter);

        // associate users data to the marathon matches
        DataScienceHelper.associateDataScienceUsers(marathonMatches, userIds);

        // push MM data to ElasticSearch
        DataScienceHelper.pushDataScience(jestClient, param, marathonMatches);
    }
}
