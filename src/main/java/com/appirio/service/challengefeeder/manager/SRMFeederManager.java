/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.manager;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appirio.service.challengefeeder.Helper;
import com.appirio.service.challengefeeder.api.SRMData;
import com.appirio.service.challengefeeder.dao.SRMFeederDAO;
import com.appirio.service.challengefeeder.dto.DataScienceFeederParam;
import com.appirio.supply.SupplyException;
import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.request.QueryParameter;
import com.appirio.tech.core.auth.AuthUser;

import io.searchbox.client.JestClient;


/**
 * SRMFeederManager is used to handle the marathon match feeder.
 * <p>
 * Added in Topcoder - Add Endpoints To Populating Marathon Matches And SRMs Into Elasticsearch v1.0
 * </p>
 *
 * <p>
 * Version 1.1 - Create CronJob For Populating Marathon Matches and SRMs To Elasticsearch v1.0
 * - Added getTimestamp method to get the current timestamp from the database
 * - Added pushSRMFeeder method to call without admin permission check
 * - Added getMatchesWithRegistrationPhaseStartedIds.
 * </p>
 *
 * @author TCSCODER
 * @version 1.1
 */
public class SRMFeederManager {

    /**
     * Logger used to log events
     */
    private static final Logger logger = LoggerFactory.getLogger(SRMFeederManager.class);

    /**
     * DAO to access SRM data from the database.
     */
    private final SRMFeederDAO srmFeederDAO;

    /**
     * The jestClient field
     */
    private final JestClient jestClient;

    /**
     * Create ChallengeFeederManager
     *
     * @param jestClient
     *            the jestClient to use
     * @param srmFeederDAO
     *            the srmFeederDAO to use
     */
    public SRMFeederManager(JestClient jestClient, SRMFeederDAO srmFeederDAO) {
        this.jestClient = jestClient;
        this.srmFeederDAO = srmFeederDAO;
    }
    
    /**
     * Push SRM feeder
     *
     * @param param
     *            the data science feeders param to use
     * @throws SupplyException
     *             if any error occurs
     */
    public void pushSRMFeeder(DataScienceFeederParam param) throws SupplyException {
        List<Long> roundIds = param.getRoundIds();
        // build query string to filter on round ids
        QueryParameter queryParameter = DataScienceHelper.buildInQueryParameter(roundIds, "roundIds");

        // fetch SRM data from persistence
        List<SRMData> srms = this.srmFeederDAO.getSRMs(queryParameter);

        // check if all SRMs with given roundIds exist
        DataScienceHelper.checkDataScienceExist(roundIds, srms);

        List<Map<String, Object>> userIds = this.srmFeederDAO.getUserIds(queryParameter);

        // associate users data to the SRMs
        DataScienceHelper.associateDataScienceUsers(srms, userIds);

        // push SRM data to ElasticSearch
        DataScienceHelper.pushDataScience(jestClient, param, srms);
    }

    /**
     * Push SRM feeder
     *
     * @param authUser
     *            the authUser to use
     * @param param
     *            the data science feeders param to use
     * @throws SupplyException
     *             if any error occurs
     */
    public void pushSRMFeeder(AuthUser authUser, DataScienceFeederParam param) throws SupplyException {
        logger.info("Enter of pushSRMFeeder(AuthUser, DataScienceFeederParam)");
        Helper.checkAdmin(authUser);
        DataScienceHelper.checkDataScienceFeederParam(param, "srms");
        pushSRMFeeder(param);
    }

    /**
     * Get the single round matches whose registration phase started after the specified date and after the last run timestamp.
     * 
     * @param date The date param.
     * @param lastRun The last run timestamp.
     * @return The list of TCID.
     */
    public List<TCID> getMatchesWithRegistrationPhaseStartedIds(java.sql.Date date, long lastRun) {
        if (date == null) {
            throw new IllegalArgumentException("The date param should be non-null.");
        }
        return this.srmFeederDAO.getMatchesWithRegistrationPhaseStartedIds(date, lastRun);
    }
}
