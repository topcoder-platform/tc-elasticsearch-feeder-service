/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.manager;

import com.appirio.service.challengefeeder.Helper;
import com.appirio.service.challengefeeder.api.ChallengeData;
import com.appirio.service.challengefeeder.api.CheckpointPrizeData;
import com.appirio.service.challengefeeder.api.EventData;
import com.appirio.service.challengefeeder.api.FileTypeData;
import com.appirio.service.challengefeeder.api.PhaseData;
import com.appirio.service.challengefeeder.api.PrizeData;
import com.appirio.service.challengefeeder.api.PropertyData;
import com.appirio.service.challengefeeder.api.ResourceData;
import com.appirio.service.challengefeeder.api.ReviewData;
import com.appirio.service.challengefeeder.api.SubmissionData;
import com.appirio.service.challengefeeder.api.TermsOfUseData;
import com.appirio.service.challengefeeder.api.WinnerData;
import com.appirio.service.challengefeeder.dao.ChallengeFeederDAO;
import com.appirio.service.challengefeeder.dto.ChallengeFeederParam;
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
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;

/**
 * ChallengeFeederManager is used to handle the challenge feeder.
 * 
 * Version 1.1 - Topcoder - Populate Marathon Match Related Data Into Challenge Model In Elasticsearch v1.0
 * - challenged to call util classes for common shared methods such as assoicate methods
 * 
 * 
 * @author TCSCODER
 * @version 1.1 
 */
public class ChallengeFeederManager {

    /**
     * Logger used to log events
     */
    private static final Logger logger = LoggerFactory.getLogger(ChallengeFeederManager.class);


    /**
     * DAO to access challenge data from the transactional database.
     */
    private final ChallengeFeederDAO challengeFeederDAO;


    /**
     * The jestClient field
     */
    private final JestClient jestClient;

    /**
     * Create ChallengeFeederManager
     *
     * @param jestClient the jestClient to use
     * @param challengeFeederDAO the challengeFeederDAO to use
     */
    public ChallengeFeederManager(JestClient jestClient, ChallengeFeederDAO challengeFeederDAO) {
        this.jestClient = jestClient;
        this.challengeFeederDAO = challengeFeederDAO;
    }

    /**
     * Push challenge feeder
     *
     * @param authUser the authUser to use
     * @param param the challenge feeders param to use
     * @throws SupplyException if any error occurs
     */
    public void pushChallengeFeeder(AuthUser authUser, ChallengeFeederParam param) throws SupplyException {
        logger.info("Enter of pushChallengeFeeder");
        Helper.checkAdmin(authUser);
        if (param.getType() == null || param.getType().trim().length() == 0) {
            param.setType("challenges");
        }
        if (param.getIndex() == null || param.getIndex().trim().length() == 0) {
            throw new SupplyException("The index should be non-null and non-empty string.", HttpServletResponse.SC_BAD_REQUEST);
        }
        if (param.getChallengeIds() == null || param.getChallengeIds().size() == 0) {
            throw new SupplyException("Challenge ids must be provided", HttpServletResponse.SC_BAD_REQUEST);
        }
        if (param.getChallengeIds().contains(null)) {
            throw new SupplyException("Null challenge id is not allowed", HttpServletResponse.SC_BAD_REQUEST);
        }

        FilterParameter filter = new FilterParameter("challengeIds=in(" + ChallengeFeederUtil.listAsString(param.getChallengeIds()) + ")");
        QueryParameter queryParameter = new QueryParameter(new FieldSelector());
        queryParameter.setFilter(filter);
        List<ChallengeData> challenges = this.challengeFeederDAO.getChallenges(queryParameter);
        
        List<Long> idsNotFound = new ArrayList<Long>();
        for (Long id : param.getChallengeIds()) {
            boolean hit = false;
            for (ChallengeData data : challenges) {
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
            throw new SupplyException("The challenge ids not found: " + idsNotFound, HttpServletResponse.SC_NOT_FOUND);
        }
        
        logger.info("Total hits:" + challenges.size());

        // associate all the data
        List<PhaseData> phases = this.challengeFeederDAO.getPhases(queryParameter);
        ChallengeFeederUtil.associateAllPhases(challenges, phases);

        List<ResourceData> resources = this.challengeFeederDAO.getResources(queryParameter);
        ChallengeFeederUtil.associateAllResources(challenges, resources);

        List<PrizeData> prizes = this.challengeFeederDAO.getPrizes(queryParameter);
        ChallengeFeederUtil.associateAllPrizes(challenges, prizes);

        List<CheckpointPrizeData> checkpointPrizes = this.challengeFeederDAO.getCheckpointPrizes(queryParameter);
        ChallengeFeederUtil.associateAllCheckpointPrizes(challenges, checkpointPrizes);

        List<PropertyData> properties = this.challengeFeederDAO.getProperties(queryParameter);
        ChallengeFeederUtil.associateAllProperties(challenges, properties);

        List<ReviewData> reviews = this.challengeFeederDAO.getReviews(queryParameter);
        ChallengeFeederUtil.associateAllReviews(challenges, reviews);

        List<SubmissionData> submissions = this.challengeFeederDAO.getSubmissions(queryParameter);
        ChallengeFeederUtil.associateAllSubmissions(challenges, submissions);

        List<WinnerData> winners = this.challengeFeederDAO.getWinners(queryParameter);
        ChallengeFeederUtil.associateAllWinners(challenges, winners);

        List<FileTypeData> fileTypes = this.challengeFeederDAO.getFileTypes(queryParameter);
        ChallengeFeederUtil.associateAllFileTypes(challenges, fileTypes);

        List<TermsOfUseData> termsOfUse = this.challengeFeederDAO.getTerms(queryParameter);
        ChallengeFeederUtil.associateAllTermsOfUse(challenges, termsOfUse);
        
        List<EventData> events = this.challengeFeederDAO.getEvents(queryParameter);
        ChallengeFeederUtil.associateAllEvents(challenges, events);

        List<Map<String, Object>> groupIds = this.challengeFeederDAO.getGroupIds(queryParameter);
        for (ChallengeData data : challenges) {
            for (Map<String, Object> item : groupIds) {
                if (item.get("challengeId").toString().equals(data.getId().toString())) {
                    if (data.getGroupIds() == null) {
                        data.setGroupIds(new ArrayList<Long>());
                    }
                    if (item.get("groupId") != null) {
                        data.getGroupIds().add(Long.parseLong(item.get("groupId").toString()));
                    }
                }
            }
        }

        List<Map<String, Object>> userIds = this.challengeFeederDAO.getUserIds(queryParameter);
        for (ChallengeData data : challenges) {
            for (Map<String, Object> item : userIds) {
                if (item.get("challengeId").toString().equals(data.getId().toString())) {
                    if (data.getUserIds() == null) {
                        data.setUserIds(new ArrayList<Long>());
                    }
                    if (data.getHasUserSubmittedForReview() == null) {
                        data.setHasUserSubmittedForReview(new ArrayList<String>());
                    }
                    data.getUserIds().add(Long.parseLong(item.get("userId").toString()));
                    data.getHasUserSubmittedForReview().add(item.get("hasUserSubmittedForReview").toString());
                }
            }
        }

        try {
            JestClientUtils.pushFeeders(jestClient, param, challenges);
        } catch (IOException ioe) {
            SupplyException se = new SupplyException("Internal server error occurs", ioe);
            se.setStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw se;
        }
    }
}
