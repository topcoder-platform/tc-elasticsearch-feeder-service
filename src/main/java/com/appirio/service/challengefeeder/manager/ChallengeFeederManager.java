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
import com.appirio.supply.constants.*;
import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.request.FieldSelector;
import com.appirio.tech.core.api.v3.request.FilterParameter;
import com.appirio.tech.core.api.v3.request.QueryParameter;
import com.appirio.tech.core.auth.AuthUser;

import io.searchbox.client.JestClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import javax.servlet.http.HttpServletResponse;

import java.util.stream.*;

/**
 * ChallengeFeederManager is used to handle the challenge feeder.
 *
 * Version 1.1 - Topcoder - Populate Marathon Match Related Data Into Challenge Model In Elasticsearch v1.0
 * - challenged to call util classes for common shared methods such as assoicate methods
 *
 * Version 1.2 - Topcoder - Create CronJob For Populating Changed Challenges To Elasticsearch v1.0
 * - add getTimestamp method to get the current timestamp from the database
 * - add pushChallengeFeeder method to call without admin permission check
 * 
 * Version 1.3 - Topcoder - Elasticsearch Service - Populate Challenge Points Prize In Challenges Index
 * - add the methods to associate the prize points for the challenge ids
 * - add the DAO calls to get prize points for the challenge ids.
 * 
 * @author TCSCODER
 * @version 1.3
 */
public class ChallengeFeederManager {

    /**
     * Logger used to log events
     */
    private static final Logger logger = LoggerFactory.getLogger(ChallengeFeederManager.class);

    /**
     * MM Round id project_info_type_id
     */
    private static final long MM_PROPERTY_ID = 56;

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
        this.pushChallengeFeeder(param);
    }
    
    /**
     * Push challenge feeder
     *
     * @param param the challenge feeders param to use
     * @throws SupplyException if any error occurs
     */
    public void pushChallengeFeeder(ChallengeFeederParam param) throws SupplyException {
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
        
        List<Long> idsNotFound = new ArrayList<>();
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
            logger.warn("These challenge ids can not be found:" + idsNotFound);
        }
        
        logger.info("aggregating challenge data for " + param.getChallengeIds());

        // associate all the data
        List<PhaseData> phases = this.challengeFeederDAO.getPhases(queryParameter);
        ChallengeFeederUtil.associateAllPhases(challenges, phases);

        List<ResourceData> resources = this.challengeFeederDAO.getResources(queryParameter);
        ChallengeFeederUtil.associateAllResources(challenges, resources);

        List<PrizeData> prizes = this.challengeFeederDAO.getPrizes(queryParameter);
        ChallengeFeederUtil.associateAllPrizes(challenges, prizes);
        
        List<PrizeData> pointPrizes = this.challengeFeederDAO.getPointsPrize(queryParameter);
        ChallengeFeederUtil.associateAllPointsPrize(challenges, pointPrizes);

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

        Map<Long,Long> mmRoundToIdMaps = new HashMap<>();
        for (ChallengeData data : challenges) {
            if (SubTrack.MARATHON_MATCH.name().equals(data.getSubTrack()) ||
                    SubTrack.DEVELOP_MARATHON_MATCH.name().equals(data.getSubTrack())) {
                if (data.getIsBanner() == null) data.setIsBanner(Boolean.FALSE);
                data.setIsLegacy(Boolean.FALSE);
                for (PropertyData prop : data.getProperties()) {
                    if (prop.getPropertyId() == MM_PROPERTY_ID && prop.getValue() != null) {
                        mmRoundToIdMaps.put(Long.valueOf(prop.getValue()), data.getId());
                        break;
                    }
                }
            }
            for (Map<String, Object> item : groupIds) {
                if (item.get("challengeId").toString().equals(data.getId().toString())) {
                    if (data.getGroupIds() == null) {
                        data.setGroupIds(new ArrayList<>());
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
                        data.setUserIds(new ArrayList<>());
                    }
                    if (data.getHasUserSubmittedForReview() == null) {
                        data.setHasUserSubmittedForReview(new ArrayList<>());
                    }
                    data.getUserIds().add(Long.parseLong(item.get("userId").toString()));
                    data.getHasUserSubmittedForReview().add(item.get("hasUserSubmittedForReview").toString());
                }
            }
        }

        //find mm contest and component id, submitter
        if (mmRoundToIdMaps.size() > 0) {
            FilterParameter roundIdFilter = new FilterParameter("roundIds=in(" +
                    String.join(", ", mmRoundToIdMaps.keySet().stream().map(r -> r.toString()).collect(Collectors.toList())) + ")");
            queryParameter.setFilter(roundIdFilter);
            List<Map<String, Object>> mmContestIds = this.challengeFeederDAO.getMMContestComponent(queryParameter);
            List<ResourceData> mmResources = this.challengeFeederDAO.getMMResources(queryParameter);
            mmContestIds.forEach(cm -> {
                ChallengeData challenge = challenges.stream().filter(c -> c.getId() == mmRoundToIdMaps.get(Long.valueOf(cm.get("roundId").toString())))
                        .findFirst().orElse(null);
                challenge.setContestId(Long.valueOf(cm.get("contestId").toString()));
                challenge.setComponentId(Long.valueOf(cm.get("componentId").toString()));
                //remove submitter if there's we'll populate from legacy mm data below
                challenge.setResources(challenge.getResources().stream()
                        .filter(r -> !"Submitter".equals(r.getRole())).collect(Collectors.toList()));
                challenge.setNumSubmissions(0L);
                challenge.setNumRegistrants(0L);
            });
            //collect submitter from legacy mm
            mmResources.forEach(r -> {
                ChallengeData challenge = challenges.stream().filter(c -> c.getId() == mmRoundToIdMaps.get(r.getChallengeId()))
                        .findFirst().orElse(null);
                if (challenge.getUserIds() == null) challenge.setUserIds(new ArrayList<>());
                challenge.getUserIds().add(r.getUserId());
                if (challenge.getHasUserSubmittedForReview() == null) challenge.setHasUserSubmittedForReview(new ArrayList<>());
                String hasSubmitted = (r.getSubmissionCount() > 0) ? r.getUserId().toString() + 'T' : r.getUserId().toString() + 'F';
                challenge.getHasUserSubmittedForReview().add(hasSubmitted);
                challenge.setNumRegistrants(challenge.getNumRegistrants() + 1);
                challenge.setNumSubmissions(challenge.getNumSubmissions() + r.getSubmissionCount());
                r.setChallengeId(null);
                challenge.getResources().add(r);
            });
        }

        logger.info("pushing challenge data to elasticsearch for " + param.getChallengeIds());

        try {
            JestClientUtils.pushFeeders(jestClient, param, challenges);
        } catch (IOException ioe) {
            SupplyException se = new SupplyException("Internal server error occurs", ioe);
            se.setStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw se;
        }
    }
    
    /**
     * Get timestamp from the persistence
     *
     * @throws SupplyException if any error occurs
     * @return the Date result
     */
    public Date getTimestamp() throws SupplyException {
        return this.challengeFeederDAO.getTimestamp().getDate();
    }

    /**
     * Get changed challenge ids
     *
     * @param lastRunTimestamp the lastRunTimestamp to use
     * @return the List<TCID> result
     */
    public List<TCID> getChangedChallengeIds(Date lastRunTimestamp) {
        if (lastRunTimestamp == null) {
            throw new IllegalArgumentException("The lastRunTimestamp should be non-null.");
        }
        return this.challengeFeederDAO.getChangedChallengeIds(lastRunTimestamp);
    }
    
    /**
     * Associate all terms of use
     *
     * @param challenges the challenges to use
     * @param termsOfUse the termsOfUse to use
     */
    private void associateAllTermsOfUse(List<ChallengeData> challenges, List<TermsOfUseData> termsOfUse) {
        for (TermsOfUseData item : termsOfUse) {
            for (ChallengeData challenge : challenges) {
                if (challenge.getId().equals(item.getChallengeId())) {
                    if (challenge.getTerms() == null) {
                        challenge.setTerms(new ArrayList<TermsOfUseData>());
                    }
                    challenge.getTerms().add(item);
                    break;
                }
            }
        }
        for (TermsOfUseData item : termsOfUse) {
            item.setChallengeId(null);
        }
    }

    /**
     * Associate all fileTypes
     *
     * @param challenges the challenges to use
     * @param fileTypes the fileTypes to use
     */
    private void associateAllFileTypes(List<ChallengeData> challenges, List<FileTypeData> fileTypes) {
        for (FileTypeData item : fileTypes) {
            for (ChallengeData challenge : challenges) {
                if (challenge.getId().equals(item.getChallengeId())) {
                    if (challenge.getFileTypes() == null) {
                        challenge.setFileTypes(new ArrayList<FileTypeData>());
                    }
                    challenge.getFileTypes().add(item);
                    break;
                }
            }
        }
        for (FileTypeData item : fileTypes) {
            item.setChallengeId(null);
        }
    }

    /**
     * Associate all winners
     *
     * @param challenges the challenges to use
     * @param winners the winners to use
     */
    private void associateAllWinners(List<ChallengeData> challenges, List<WinnerData> winners) {
        for (WinnerData item : winners) {
            for (ChallengeData challenge : challenges) {
                if (challenge.getId().equals(item.getChallengeId())) {
                    if (challenge.getWinners() == null) {
                        challenge.setWinners(new ArrayList<WinnerData>());
                    }
                    challenge.getWinners().add(item);
                    break;
                }
            }
        }
        for (WinnerData item : winners) {
            item.setChallengeId(null);
        }
    }

    /**
     * Associate all submissions
     *
     * @param challenges the challenges to use
     * @param submissions the submissions to use
     */
    private void associateAllSubmissions(List<ChallengeData> challenges, List<SubmissionData> submissions) {
        for (SubmissionData item : submissions) {
            for (ChallengeData challenge : challenges) {
                if (challenge.getId().equals(item.getChallengeId())) {
                    if (challenge.getSubmissions() == null) {
                        challenge.setSubmissions(new ArrayList<SubmissionData>());
                    }
                    challenge.getSubmissions().add(item);
                    break;
                }
            }
        }
        for (SubmissionData item : submissions) {
            item.setChallengeId(null);
        }
    }

    /**
     * Associate all reviews
     *
     * @param challenges the challenges to use
     * @param reviews the reviews to use
     */
    private void associateAllReviews(List<ChallengeData> challenges, List<ReviewData> reviews) {
        for (ReviewData item : reviews) {
            for (ChallengeData challenge : challenges) {
                if (challenge.getId().equals(item.getChallengeId())) {
                    if (challenge.getReviews() == null) {
                        challenge.setReviews(new ArrayList<ReviewData>());
                    }
                    challenge.getReviews().add(item);
                    break;
                }
            }
        }
        for (ReviewData item : reviews) {
            item.setChallengeId(null);
        }
    }

    /**
     * Associate all properties
     *
     * @param challenges the challenges to use
     * @param properties the properties to use
     */
    private void associateAllProperties(List<ChallengeData> challenges, List<PropertyData> properties) {
        for (PropertyData item : properties) {
            for (ChallengeData challenge : challenges) {
                if (challenge.getId().equals(item.getChallengeId())) {
                    if (challenge.getProperties() == null) {
                        challenge.setProperties(new ArrayList<PropertyData>());
                    }
                    challenge.getProperties().add(item);
                    break;
                }
            }
        }
        for (PropertyData item : properties) {
            item.setChallengeId(null);
        }
    }

    /**
     * Associate all checkpointPrizes
     *
     * @param challenges the challenges to use
     * @param checkpointPrizes the checkpointPrizes to use
     */
    private void associateAllCheckpointPrizes(List<ChallengeData> challenges, List<CheckpointPrizeData> checkpointPrizes) {
        for (CheckpointPrizeData item : checkpointPrizes) {
            for (ChallengeData challenge : challenges) {
                if (challenge.getId().equals(item.getChallengeId())) {
                    if (challenge.getCheckpointPrizes() == null) {
                        challenge.setCheckpointPrizes(new ArrayList<CheckpointPrizeData>());
                    }
                    challenge.getCheckpointPrizes().add(item);
                    break;
                }
            }
        }
        for (CheckpointPrizeData item : checkpointPrizes) {
            item.setChallengeId(null);
        }
    }

    /**
     * Associate all prizes
     *
     * @param challenges the challenges to use
     * @param prizes the prizes to use
     */
    private void associateAllPrizes(List<ChallengeData> challenges, List<PrizeData> prizes) {
        for (PrizeData item : prizes) {
            for (ChallengeData challenge : challenges) {
                if (challenge.getId().equals(item.getChallengeId())) {
                    if (challenge.getPrizes() == null) {
                        challenge.setPrizes(new ArrayList<PrizeData>());
                    }
                    challenge.getPrizes().add(item);
                    break;
                }
            }
        }
        for (PrizeData item : prizes) {
            item.setChallengeId(null);
        }
    }

    /**
     * Associate all events
     *
     * @param challenges the challenges to use
     * @param events the events to use
     */
    private void associateAllEvents(List<ChallengeData> challenges, List<EventData> events) {
        for (EventData item : events) {
            for (ChallengeData challenge : challenges) {
                if (challenge.getId().equals(item.getChallengeId())) {
                    if (challenge.getEvents() == null) {
                        challenge.setEvents(new ArrayList<EventData>());
                    }
                    challenge.getEvents().add(item);
                    break;
                }
            }
        }
        for (EventData item : events) {
            item.setChallengeId(null);
        }
    }

    /**
     * Associate all phases
     *
     * @param challenges the challenges to use
     * @param allPhases the allPhases to use
     */
    private void associateAllPhases(List<ChallengeData> challenges, List<PhaseData> allPhases) {
        for (PhaseData aPhase : allPhases) {
            for (ChallengeData challenge : challenges) {
                if (challenge.getId().equals(aPhase.getChallengeId())) {
                    if (challenge.getPhases() == null) {
                        challenge.setPhases(new ArrayList<PhaseData>());
                    }
                    challenge.getPhases().add(aPhase);
                    break;
                }
            }
        }
        for (PhaseData aPhase : allPhases) {
            aPhase.setChallengeId(null);
        }
    }

    /**
     * Associate all resources
     *
     * @param challenges the challenges to use
     * @param resources the resources to use
     */
    private void associateAllResources(List<ChallengeData> challenges, List<ResourceData> resources) {
        for (ResourceData item : resources) {
            for (ChallengeData challenge : challenges) {
                if (challenge.getId().equals(item.getChallengeId())) {
                    if (challenge.getResources() == null) {
                        challenge.setResources(new ArrayList<ResourceData>());
                    }
                    challenge.getResources().add(item);
                    break;
                }
            }
        }
        for (ResourceData item : resources) {
            item.setChallengeId(null);
        }
    }
}
