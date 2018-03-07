/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.manager;

import com.appirio.service.challengefeeder.Helper;
import com.appirio.service.challengefeeder.api.ChallengeData;
import com.appirio.service.challengefeeder.api.EventData;
import com.appirio.service.challengefeeder.api.PhaseData;
import com.appirio.service.challengefeeder.api.PrizeData;
import com.appirio.service.challengefeeder.api.ResourceData;
import com.appirio.service.challengefeeder.api.SubmissionData;
import com.appirio.service.challengefeeder.api.TermsOfUseData;
import com.appirio.service.challengefeeder.dao.MmFeederDAO;
import com.appirio.service.challengefeeder.dto.MmFeederParam;
import com.appirio.service.challengefeeder.util.JestClientUtils;
import com.appirio.supply.SupplyException;
import com.appirio.tech.core.api.v3.request.FieldSelector;
import com.appirio.tech.core.api.v3.request.FilterParameter;
import com.appirio.tech.core.api.v3.request.QueryParameter;
import com.appirio.tech.core.auth.AuthUser;

import io.searchbox.client.JestClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * MmFeederManager is used to handle the marathon match feeders.
 * 
 * It's added in Topcoder - Populate Marathon Match Related Data Into Challenge Model In Elasticsearch v1.0
 * 
 * @author TCSCODER
 * @version 1.0
 */
public class MmFeederManager {
    
    /**
     * Logger used to log events
     */
    private static final Logger logger = LoggerFactory.getLogger(MmFeederManager.class);


    /**
     * DAO to access marathon match data from the transactional database.
     */
    private final MmFeederDAO mmFeederDAO;


    /**
     * The jestClient field
     */
    private final JestClient jestClient;

    /**
     * Create MmFeederManager
     *
     * @param jestClient the jestClient to use
     * @param mmFeederDAO the mmFeederDAO to use
     */
    public MmFeederManager(JestClient jestClient, MmFeederDAO mmFeederDAO) {
        this.jestClient = jestClient;
        this.mmFeederDAO = mmFeederDAO;
    }
    
    /**
     * Push marathon match data into challenge model in elasticsearch.
     *
     * @param authUser the authUser to use
     * @param param the param to use
     * @throws SupplyException if any error occurs
     */
    public void pushMarathonMatchDataIntoChallenge(AuthUser authUser, MmFeederParam param) throws SupplyException {
        logger.info("Enter of pushMarathonMatchDataIntoChallenge");
        Helper.checkAdmin(authUser);
        checkMarathonFeederParam(param, "challenges");
        
        FilterParameter filter = new FilterParameter("roundIds=in(" + ChallengeFeederUtil.listAsString(param.getRoundIds()) + ")");
        QueryParameter queryParameter = new QueryParameter(new FieldSelector());
        queryParameter.setFilter(filter);
        List<ChallengeData> mms = this.mmFeederDAO.getMarathonMatches(queryParameter);
        
        checkMissedIds(param, mms);
        
        // associate all the data
        List<PhaseData> phases = this.mmFeederDAO.getPhases(queryParameter);
        for (PhaseData data : phases) {
            if (data.getActualStartTime() != null && data.getActualEndTime() != null) {
                data.setDuration((data.getActualEndTime().getTime() - data.getActualStartTime().getTime()) / 1000);
            }
        }
        ChallengeFeederUtil.associateAllPhases(mms, phases);

        List<PrizeData> prizes = this.mmFeederDAO.getPrizes(queryParameter);
        ChallengeFeederUtil.associateAllPrizes(mms, prizes);

        List<SubmissionData> submissions = this.mmFeederDAO.getSubmissions(queryParameter);
        ChallengeFeederUtil.associateAllSubmissions(mms, submissions);

        List<TermsOfUseData> termsOfUse = this.mmFeederDAO.getTerms(queryParameter);
        ChallengeFeederUtil.associateAllTermsOfUse(mms, termsOfUse);
        
        List<EventData> events = this.mmFeederDAO.getEvents(queryParameter);
        ChallengeFeederUtil.associateAllEvents(mms, events);
        
        List<ResourceData> resources = this.mmFeederDAO.getResources(queryParameter);
        // set the user ids before associating the resources as the associate method will set the challenge id to null
        for (ChallengeData data : mms) {
            for (ResourceData resourceData : resources) {
                if (data.getId().longValue() == resourceData.getChallengeId().longValue()) {
                    if (data.getUserIds() == null) {
                        data.setUserIds(new ArrayList<Long>());
                    }
                    data.getUserIds().add(resourceData.getUserId());
                }
            }
        }
        ChallengeFeederUtil.associateAllResources(mms, resources);
        
        try {
            JestClientUtils.pushFeeders(jestClient, param, mms);
        } catch (IOException ioe) {
            SupplyException se = new SupplyException("Internal server error occurs", ioe);
            se.setStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw se;
        }
    }
    
    /**
     * Check missed ids
     *
     * @param param the param to use
     * @param result the result to use
     * @throws SupplyException if any error occurs
     */
    private static void checkMissedIds(MmFeederParam param, List<ChallengeData> result) throws SupplyException {
        List<Long> idsNotFound = new ArrayList<Long>();
        for (Long id : param.getRoundIds()) {
            boolean hit = false;
            for (ChallengeData data : result) {
                if (id.longValue() == data.getId().longValue()) {
                    hit = true;
                    break;
                }
            }
            if (!hit) {
                idsNotFound.add(id);
            }
        }
        if (!idsNotFound.isEmpty()) {
            throw new SupplyException("The round ids not found: " + idsNotFound, HttpServletResponse.SC_NOT_FOUND);
        }

    }

    /**
     * Check marathon feeder parameter
     *
     * @param param the param to use
     * @param defaultType the defaultType to use
     * @throws SupplyException if any error occurs
     */
    private static void checkMarathonFeederParam(MmFeederParam param, String defaultType) throws SupplyException {
        if (param.getType() == null || param.getType().trim().length() == 0) {
            param.setType(defaultType);
        }
        if (param.getIndex() == null || param.getIndex().trim().length() == 0) {
            throw new SupplyException("The index should be non-null and non-empty string.", HttpServletResponse.SC_BAD_REQUEST);
        }

        if (param.getRoundIds() == null || param.getRoundIds().size() == 0) {
            throw new SupplyException("Round ids must be provided", HttpServletResponse.SC_BAD_REQUEST);
        }
        if (param.getRoundIds().contains(null)) {
            throw new SupplyException("Null round id is not allowed", HttpServletResponse.SC_BAD_REQUEST);
        }
        
        Set<Long> duplicateIds = new HashSet<Long>();
        for (Long id : param.getRoundIds()) {
            if (id.longValue() <= 0) {
                throw new SupplyException("Round id should be positive", HttpServletResponse.SC_BAD_REQUEST);
            }
            if (param.getRoundIds().indexOf(id) != param.getRoundIds().lastIndexOf(id)) {
                duplicateIds.add(id);
            }
        }
        
        if (!duplicateIds.isEmpty()) {
            throw new SupplyException("The round ids are duplicate:" + duplicateIds, HttpServletResponse.SC_BAD_REQUEST);
        }
    }

}
