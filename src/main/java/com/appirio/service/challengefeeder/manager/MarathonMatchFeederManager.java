/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.manager;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appirio.service.challengefeeder.Helper;
import com.appirio.service.challengefeeder.api.MarathonMatchData;
import com.appirio.service.challengefeeder.dao.MarathonMatchFeederDAO;
import com.appirio.service.challengefeeder.dto.DataScienceFeederParam;
import com.appirio.supply.SupplyException;
import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.request.QueryParameter;
import com.appirio.tech.core.auth.AuthUser;

import io.searchbox.client.JestClient;


/**
 * MarathonMatchFeederManager is used to handle the marathon match feeder.
 * 
 * <p>
 * Added in Topcoder - Add Endpoints To Populating Marathon Matches And SRMs Into Elasticsearch v1.0
 * </p>
 * 
 * <p>
 * Version 1.1 - Create CronJob For Populating Marathon Matches and SRMs To Elasticsearch v1.0
 * - Added getTimestamp method to get the current timestamp from the database
 * - Added pushMarathonMatchFeeder method to call without admin permission check
 * - Added getMatchesWithRegistrationPhaseStartedIds.
 * </p>
 *
 * @author TCSCODER
 * @version 1.1
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
     * Push marathon match feeder.
     *
     * @param param
     *            the data science feeders param to use
     * @throws SupplyException
     *             if any error occurs
     */
    public void pushMarathonMatchFeeder(DataScienceFeederParam param) throws SupplyException {
        logger.info("Enter of pushMarathonMatchFeeder(DataScienceFeederParam)");
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

    /**
     * Push marathon match feeder.
     *
     * @param authUser
     *            the authUser to use
     * @param param
     *            the data science feeders param to use
     * @throws SupplyException
     *             if any error occurs
     */
    public void pushMarathonMatchFeeder(AuthUser authUser, DataScienceFeederParam param) throws SupplyException {
        logger.info("Enter of pushMarathonMatchFeeder(AuthUser, DataScienceFeederParam)");
        Helper.checkAdmin(authUser);
        pushMarathonMatchFeeder(param);
    }
    
    /**
     * Get current timestamp from the database.
     *
     * @throws SupplyException if any error occurs
     * @return the timestamp result
     */
    public Date getTimestamp() throws SupplyException {
        return this.marathonMatchFeederDAO.getTimestamp().getDate();
    }

    /**
     * Get the marathon matches whose registration phase started after the specified date and after the last run timestamp.
     * 
     * @param date The date param.
     * @param lastRun The last run timestamp.
     * @return The list of TCID.
     */
    public List<TCID> getMatchesWithRegistrationPhaseStartedIds(java.sql.Date date, long lastRun) {
        if (date == null) {
            throw new IllegalArgumentException("The date param should be non-null.");
        }
        return this.marathonMatchFeederDAO.getMatchesWithRegistrationPhaseStartedIds(date, lastRun);
    }
}
