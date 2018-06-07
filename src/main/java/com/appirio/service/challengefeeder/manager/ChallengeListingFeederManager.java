/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appirio.service.challengefeeder.api.FileTypeData;
import com.appirio.service.challengefeeder.api.PhaseData;
import com.appirio.service.challengefeeder.api.PrizeData;
import com.appirio.service.challengefeeder.api.UserIdData;
import com.appirio.service.challengefeeder.api.challengelisting.ChallengeListingData;
import com.appirio.service.challengefeeder.api.challengelisting.EventData;
import com.appirio.service.challengefeeder.api.challengelisting.WinnerData;
import com.appirio.service.challengefeeder.config.ChallengeConfiguration;
import com.appirio.service.challengefeeder.dao.ChallengeFeederDAO;
import com.appirio.service.challengefeeder.dao.ChallengeListingFeederDAO;
import com.appirio.service.challengefeeder.dto.ChallengeFeederParam;
import com.appirio.service.challengefeeder.util.JestClientUtils;
import com.appirio.supply.SupplyException;
import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.request.FieldSelector;
import com.appirio.tech.core.api.v3.request.FilterParameter;
import com.appirio.tech.core.api.v3.request.QueryParameter;

import io.searchbox.client.JestClient;

/**
 * ChallengeListingFeederManager is used to handle the challenge feeder for listing.
 * 
 * It's added in Topcoder ElasticSearch Feeder Service - Way To Populate Challenge-Listing Index v1.0
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
public class ChallengeListingFeederManager {
    /**
     * Logger used to log events
     */
    private static final Logger logger = LoggerFactory.getLogger(ChallengeListingFeederManager.class);
    
    /**
     * the constant for the current phase status name
     */
    private static final String PHASE_OPEN = "Open";
    
    /**
     * The constant for the upcoming phase status name
     */
    private static final String PHASE_SCHEDULED = "Scheduled";

    /**
     * The constant for the design challenge type
     */
    private static final String DESIGN_TYPE = "DESIGN";

    /**
     * DAO to access challenge data from the transactional database.
     */
    private final ChallengeFeederDAO challengeFeederDAO;
    
    /**
     * DAO to access challenge data from the transactional database.
     */
    private final ChallengeListingFeederDAO challengeListingFeederDAO;

    /**
     * The challenge configuration field
     */
    private final ChallengeConfiguration challengeConfiguration;

    /**
     * The jestClient field
     */
    private final JestClient jestClient;

    /**
     * Create ChallengeListingFeederManager
     *
     * @param jestClient the jestClient to use
     * @param challengeFeederDAO the challengeFeederDAO to use
     * @param challengeListingFeederDAO the challengeListingFeederDAO to use
     * @param challengeConfiguration the challengeConfiguration to use
     */
    public ChallengeListingFeederManager(JestClient jestClient, ChallengeFeederDAO challengeFeederDAO, ChallengeListingFeederDAO challengeListingFeederDAO, 
            ChallengeConfiguration challengeConfiguration) {
        this.jestClient = jestClient;
        this.challengeFeederDAO = challengeFeederDAO;
        this.challengeListingFeederDAO = challengeListingFeederDAO;
        this.challengeConfiguration = challengeConfiguration;
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
        List<ChallengeListingData> challenges = this.challengeListingFeederDAO.getChallenges(queryParameter);

        List<Long> ids = challenges.stream().map(c -> c.getId()).collect(Collectors.toList());
        List<Long> idsNotFound = param.getChallengeIds().stream().filter(id -> !ids.contains(id)).collect(Collectors.toList());

        if (!idsNotFound.isEmpty()) {
            logger.warn("These challenge ids can not be found:" + idsNotFound);

            ids.removeAll(idsNotFound);
        }

        List<EventData> events = this.challengeListingFeederDAO.getEventsListing(queryParameter);
        associateAllEvents(challenges, events);

        List<PhaseData> phases = this.challengeFeederDAO.getPhases(queryParameter);
        associateAllPhases(challenges, phases);

        List<PrizeData> prizes = this.challengeFeederDAO.getPrizes(queryParameter);
        associateAllPrizes(challenges, prizes);

        List<PrizeData> pointPrizes = this.challengeFeederDAO.getPointsPrize(queryParameter);
        associatePointPrizes(challenges, pointPrizes);

        List<FileTypeData> fileTypes = this.challengeFeederDAO.getFileTypes(queryParameter);
        associateAllFileTypes(challenges, fileTypes);

        List<WinnerData> winners = this.challengeListingFeederDAO.getWinnersForChallengeListing(queryParameter);
        associateAllWinners(challenges, winners);

        List<Map<String, Object>> checkpointsSubmissions = this.challengeListingFeederDAO.getCheckpointsSubmissions(queryParameter);
        List<Map<String, Object>> groupIds = this.challengeFeederDAO.getGroupIds(queryParameter);
        List<UserIdData> userIds = this.challengeListingFeederDAO.getChallengeUserIds(queryParameter);
        associateAllUserIds(challenges, userIds);

        List<Map<String, Object>> platforms = this.challengeFeederDAO.getChallengePlatforms(queryParameter);
        for (Map<String, Object> item : platforms) {
            for (ChallengeListingData data : challenges) {
                if (data.getChallengeId() == Long.parseLong(item.get("challengeId").toString())) {
                    if (data.getPlatforms() == null) {
                        data.setPlatforms(new ArrayList<>());
                    }
                    data.getPlatforms().add(item.get("name").toString());
                }
            }
        }

        List<Map<String, Object>> technologies = this.challengeFeederDAO.getChallengeTechnologies(queryParameter);
        for (Map<String, Object> item : technologies) {
            for (ChallengeListingData data : challenges) {
                if (data.getChallengeId() == Long.parseLong(item.get("challengeId").toString())) {
                    if (data.getTechnologies() == null) {
                        data.setTechnologies(new ArrayList<>());
                    }
                    data.getTechnologies().add(item.get("name").toString());
                }
            }
        }

        // set other field
        for (ChallengeListingData data : challenges) {
            boolean isStudio = DESIGN_TYPE.equalsIgnoreCase(data.getTrack().trim());
            if (isStudio) {
                if (data.getForumType() != null && DESIGN_TYPE.equalsIgnoreCase(data.getForumType().trim())) {
                    data.setForumLink(this.challengeConfiguration.getDevelopForumsUrlPrefix() + data.getForumId());
                } else {
                    data.setForumLink(this.challengeConfiguration.getStudioForumsUrlPrefix() + data.getForumId());
                }
            } else {
                data.setForumLink(this.challengeConfiguration.getDevelopForumsUrlPrefix() + data.getForumId());
            }
            data.setForumType(null);
            
            data.setDirectUrl(this.challengeConfiguration.getDirectProjectLink() + data.getChallengeId());
            
            for (Map<String, Object> item : checkpointsSubmissions) {
                if (data.getId() == Long.parseLong(item.get("challengeId").toString())) {
                    data.setNumberOfCheckpointSubmissions(Integer.parseInt(item.get("numberOfSubmissions").toString()));
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

        List<Map<String, Object>> submitterIds = challengeListingFeederDAO.getSubmitterIds(queryParameter);
        ChallengeFeederUtil.associateSubmitterIds(challenges, submitterIds);

        logger.info("pushFeeders");
        try {
            JestClientUtils.pushFeeders(jestClient, param, challenges);
        } catch (IOException ioe) {
            ioe.printStackTrace();
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
     * Associate all winners
     *
     * @param challenges the challenges to use
     * @param winners the winners to use
     */
    private static void associateAllWinners(List<ChallengeListingData> challenges, List<WinnerData> winners) {
        for (WinnerData item : winners) {
            for (ChallengeListingData challenge : challenges) {
                if (challenge.getId().equals(item.getChallengeId())) {
                    if (challenge.getWinners() == null) {
                        challenge.setWinners(new ArrayList<>());
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
     * Associate all fileTypes
     *
     * @param challenges the challenges to use
     * @param fileTypes the fileTypes to use
     */
    private static void associateAllFileTypes(List<ChallengeListingData> challenges, List<FileTypeData> fileTypes) {
        for (FileTypeData item : fileTypes) {
            for (ChallengeListingData challenge : challenges) {
                if (challenge.getId().equals(item.getChallengeId())) {
                    if (challenge.getFileTypes() == null) {
                        challenge.setFileTypes(new ArrayList<>());
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
     * Associate all prizes
     *
     * @param challenges the challenges to use
     * @param prizes the prizes to use
     */
    private static void associateAllPrizes(List<ChallengeListingData> challenges, List<PrizeData> prizes) {
        for (PrizeData item : prizes) {
            for (ChallengeListingData challenge : challenges) {
                if (challenge.getId().equals(item.getChallengeId())) {
                    if (challenge.getPrize() == null) {
                        challenge.setPrize(new ArrayList<>());
                    }
                    challenge.getPrize().add(item.getAmount());
                    break;
                }
            }
        }
    }

    /**
     * Associate point prizes
     *
     * @param challenges the challenges to use
     * @param points the point prizes to use
     */
    private static void associatePointPrizes(List<ChallengeListingData> challenges, List<PrizeData> points) {
        for (PrizeData item : points) {
            for (ChallengeListingData challenge : challenges) {
                if (challenge.getId().equals(item.getChallengeId())) {
                    if (challenge.getPointPrizes() == null) {
                        challenge.setPointPrizes(new ArrayList<>());
                    }
                    challenge.getPointPrizes().add(item.getAmount());
                    break;
                }
            }
        }
    }

    /**
     * Associate all events
     *
     * @param challenges the challenges to use
     * @param events the events to use
     */
    private static void associateAllEvents(List<ChallengeListingData> challenges, List<EventData> events) {
        for (EventData item : events) {
            for (ChallengeListingData challenge : challenges) {
                if (challenge.getId().equals(item.getChallengeId())) {
                    if (challenge.getEvents() == null) {
                        challenge.setEvents(new ArrayList<>());
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
    private static void associateAllPhases(List<ChallengeListingData> challenges, List<PhaseData> allPhases) {
        for (PhaseData aPhase : allPhases) {
            for (ChallengeListingData challenge : challenges) {
                if (challenge.getId().equals(aPhase.getChallengeId())) {
                    if (challenge.getPhases() == null) {
                        challenge.setPhases(new ArrayList<>());
                    }
                    challenge.getPhases().add(aPhase);
                    
                    if (challenge.getCurrentPhases() == null) {
                        challenge.setCurrentPhases(new ArrayList<>());
                    }
                    if (PHASE_OPEN.equalsIgnoreCase(aPhase.getStatus())) {
                        challenge.getCurrentPhases().add(aPhase);
                    } else if (PHASE_SCHEDULED.equalsIgnoreCase(aPhase.getStatus())) {
                        if (challenge.getUpcomingPhase() == null) {
                            challenge.setUpcomingPhase(aPhase);
                        } else if (challenge.getUpcomingPhase().getScheduledStartTime().getTime() > aPhase.getScheduledStartTime().getTime()) {
                            challenge.setUpcomingPhase(aPhase);
                        }
                        
                    }
                    
                    break;
                }
            }
        }
        for (PhaseData aPhase : allPhases) {
            aPhase.setChallengeId(null);
            aPhase.setUpdatedAt(null);
            aPhase.setUpdatedBy(null);
            aPhase.setCreatedAt(null);
            aPhase.setCreatedBy(null);
            aPhase.setPhaseId(null);
            aPhase.setDuration(null);
            aPhase.setFixedStartTime(null);
        }
    }
    
    
    /**
     * Associate all user ids
     *
     * @param challenges the challenges to use
     * @param userIds the userIds to use
     */
    private static void associateAllUserIds(List<ChallengeListingData> challenges, List<UserIdData> userIds) {
        for (UserIdData item : userIds) {
            for (ChallengeListingData challenge : challenges) {
                if (challenge.getId().equals(item.getChallengeId())) {
                    if (challenge.getUserIds() == null) {
                        challenge.setUserIds(new ArrayList<>());
                    }
                    challenge.getUserIds().add(item.getUserId());
                    break;
                }
            }
        }
        for (UserIdData item : userIds) {
            item.setChallengeId(null);
        }
    }
}
