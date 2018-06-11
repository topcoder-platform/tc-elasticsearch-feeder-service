/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.manager;

import com.appirio.service.challengefeeder.api.EventData;
import com.appirio.service.challengefeeder.api.PhaseData;
import com.appirio.service.challengefeeder.api.PrizeData;
import com.appirio.service.challengefeeder.api.challengelisting.ChallengeListingData;
import com.appirio.service.challengefeeder.api.challengelisting.WinnerData;
import com.appirio.service.challengefeeder.dao.ChallengeListingMMFeederDAO;
import com.appirio.service.challengefeeder.dto.MmFeederParam;
import com.appirio.service.challengefeeder.util.JestClientUtils;
import com.appirio.supply.SupplyException;
import com.appirio.supply.constants.SubTrack;
import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.request.FieldSelector;
import com.appirio.tech.core.api.v3.request.FilterParameter;
import com.appirio.tech.core.api.v3.request.QueryParameter;

import io.searchbox.client.JestClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import javax.servlet.http.HttpServletResponse;

/**
 * ChallengeListingMMFeederManager is used to handle the marathon match feeders for challenge listing.
 * 
 * It's added in Topcoder ElasticSearch Feeder Service - Way To Populate Challenge-Listing Index For Legacy Marathon Matches v1.0
 * 
 * Version 1.1 - Topcoder Elasticsearch Feeder Service - Jobs Cleanup And Improvement v1.0
 * - remove the useless dao 
 * 
 * @author TCSCODER
 * @version 1.1 
 */
public class ChallengeListingMMFeederManager {
    
    /**
     * Logger used to log events
     */
    private static final Logger logger = LoggerFactory.getLogger(ChallengeListingMMFeederManager.class);
    
    /**
     * the constant for the current phase status name
     */
    private static final String PHASE_OPEN = "Open";
    
    /**
     * The constant for the upcoming phase status name
     */
    private static final String PHASE_SCHEDULED = "Scheduled";
    
    /**
     * DAO to access marathon match data from the transactional database.
     */
    private final ChallengeListingMMFeederDAO challengeListingMmFeederDAO;

    /**
     * The jestClient field
     */
    private final JestClient jestClient;
    
    /**
     * The forum link url field
     */
    private final String forumLinkUrl;

    /**
     * Create ChallengeListingMMFeederManager
     *
     * @param jestClient the jestClient to use
     * @param challengeListingMmFeederDAO the challengeListingMmFeederDAO to use
     * @param forumLinkUrl the forumLinkUrl to use
     */
    public ChallengeListingMMFeederManager(JestClient jestClient, ChallengeListingMMFeederDAO challengeListingMmFeederDAO, String forumLinkUrl) {
        this.jestClient = jestClient;
        this.challengeListingMmFeederDAO = challengeListingMmFeederDAO;
        this.forumLinkUrl = forumLinkUrl;
    }

    /**
     * Push marathon match data into challenge listing model in elasticsearch.
     *
     * @param param the param to use
     * @throws SupplyException if any error occurs
     */
    public void pushMarathonMatchDataIntoChallenge(MmFeederParam param) throws SupplyException {
        DataScienceHelper.checkMarathonFeederParam(param, "challenges");

        FilterParameter filter = new FilterParameter("roundIds=in(" + ChallengeFeederUtil.listAsString(param.getRoundIds()) + ")");
        QueryParameter queryParameter = new QueryParameter(new FieldSelector());
        queryParameter.setFilter(filter);
        List<ChallengeListingData> mms = this.challengeListingMmFeederDAO.getMarathonMatches(queryParameter);

        List<Long> ids = mms.stream().map(c -> c.getId()).collect(Collectors.toList());
        List<Long> idsNotFound = param.getRoundIds().stream().filter(id -> !ids.contains(id)).collect(Collectors.toList());

        if (!idsNotFound.isEmpty()) {
            logger.warn("These challenge ids can not be found:" + idsNotFound);

            ids.removeAll(idsNotFound);
        }
        
        // associate all the data
        List<PhaseData> phases = this.challengeListingMmFeederDAO.getPhases(queryParameter);
        associateAllPhases(mms, phases);

        List<PrizeData> prizes = this.challengeListingMmFeederDAO.getPrizes(queryParameter);
        associateAllPrizes(mms, prizes);
        
        List<EventData> events = this.challengeListingMmFeederDAO.getEvents(queryParameter);
        associateAllEvents(mms, events);
        
        List<WinnerData> winners = this.challengeListingMmFeederDAO.getMarathonMatchWinners(queryParameter);
        associateAllWinners(mms, winners);

        List<Map<String, Object>> submitterIds = this.challengeListingMmFeederDAO.getSubmitterIds(queryParameter);
        ChallengeFeederUtil.associateSubmitterIds(mms, submitterIds);
        
        mms.forEach(c -> {
            if (c.getForumId() != null) {
                c.setForumLink(forumLinkUrl + c.getForumId());
            }
            c.setSubTrackFromEnum(SubTrack.MARATHON_MATCH);
            c.setIsTask(false);
            if (c.getNumberOfSubmissions() == null) {
                c.setNumberOfSubmissions(0);
            }
            if (c.getTotalPrize() == null) {
                c.setTotalPrize(0.0);
            }
            if (c.getStatus() != null) {
                c.setStatus(c.getStatus().trim());
            }
        });
        
        try {
            JestClientUtils.pushFeeders(jestClient, param, mms);
        } catch (IOException ioe) {
            SupplyException se = new SupplyException("Internal server error occurs", ioe);
            se.setStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw se;
        }
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
                    com.appirio.service.challengefeeder.api.challengelisting.EventData data = new com.appirio.service.challengefeeder.api.challengelisting.EventData();
                    data.setEventDescription(item.getEventName());
                    data.setEventShortDesc(item.getEventName());
                    data.setId(item.getEventId());
                    challenge.getEvents().add(data);
                    break;
                }
            }
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
        }
    }

    /**
     * Get the marathon matches whose registration phase started after the specified date and after the last run timestamp.
     *
     * @param date The date param.
     * @param lastRunTimestamp The last run timestamp.
     * @return The list of TCID.
     */
    public List<TCID> getMatchesWithRegistrationPhaseStartedIds(java.sql.Date date, long lastRunTimestamp) {
        return this.challengeListingMmFeederDAO.getMatchesWithRegistrationPhaseStartedIds(date, lastRunTimestamp);
    }
}
