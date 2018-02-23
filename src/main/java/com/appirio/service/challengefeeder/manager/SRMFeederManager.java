/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.manager;

import com.appirio.service.challengefeeder.Helper;
import com.appirio.service.challengefeeder.api.SRMData;
import com.appirio.service.challengefeeder.dao.SRMFeederDAO;
import com.appirio.service.challengefeeder.dto.DataScienceFeederParam;
import com.appirio.supply.SupplyException;
import com.appirio.tech.core.api.v3.request.QueryParameter;
import com.appirio.tech.core.auth.AuthUser;

import io.searchbox.client.JestClient;
import io.searchbox.core.Bulk;
import io.searchbox.core.Bulk.Builder;
import io.searchbox.core.Delete;
import io.searchbox.core.Index;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;


/**
 * SRMFeederManager is used to handle the marathon match feeder.
 * <p>
 * Added in Topcoder - Add Endpoints To Populating Marathon Matches And SRMs Into Elasticsearch v1.0
 * </p>
 *
 * @author TCSCODER
 * @version 1.0
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
     * @param authUser
     *            the authUser to use
     * @param param
     *            the data science feeders param to use
     * @throws SupplyException
     *             if any error occurs
     */
    public void pushSRMFeeder(AuthUser authUser, DataScienceFeederParam param) throws SupplyException {
        logger.info("Enter of pushSRMFeeder");
        Helper.checkAdmin(authUser);
        DataScienceHelper.checkDataScienceFeederParam(param, "srms");

        List<Long> roundIds = param.getRoundIds();
        // build query string to filter on round ids
        QueryParameter queryParameter = DataScienceHelper.buildInQueryParameter(roundIds, "roundIds");

        // fetch SRM data from persistence
        List<SRMData> srms = this.srmFeederDAO.getSRMs(queryParameter);

        // check if all SRMs with given roundIds exist
        DataScienceHelper.checkDataScienceExist(roundIds, srms);

        logger.info("Total hits:" + srms.size());

        List<Map<String, Object>> userIds = this.srmFeederDAO.getUserIds(queryParameter);

        // associate users data to the SRMs
        DataScienceHelper.associateDataScienceUsers(srms, userIds);

        // push SRM data to ElasticSearch
        DataScienceHelper.pushDataScience(jestClient, param, srms);
    }
}
