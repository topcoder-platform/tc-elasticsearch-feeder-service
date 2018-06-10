/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.manager;

import com.appirio.service.challengefeeder.api.ResourceData;
import com.appirio.service.challengefeeder.api.challengedetail.ChallengeDetailData;
import com.appirio.service.challengefeeder.api.challengedetail.RegistrantData;
import com.appirio.service.challengefeeder.api.challengedetail.SubmissionData;
import com.appirio.service.challengefeeder.dao.ChallengeDetailMMFeederDAO;
import com.appirio.service.challengefeeder.dto.MmFeederParam;
import com.appirio.service.challengefeeder.util.JestClientUtils;
import com.appirio.supply.SupplyException;
import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.request.FieldSelector;
import com.appirio.tech.core.api.v3.request.FilterParameter;
import com.appirio.tech.core.api.v3.request.QueryParameter;

import io.searchbox.client.JestClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

/**
 * ChallengeDetailMMFeederManager is used to handle the marathon match feeders for challenge detail.
 * 
 * It's added in Topcoder ElasticSearch Feeder Service - Way To Populate Challenge-Detail Index For Legacy Marathon Matches v1.0
 * 
 * @author TCSCODER
 * @version 1.0
 */
public class ChallengeDetailMMFeederManager {
    /**
     * Logger used to log events
     */
    private static final Logger logger = LoggerFactory.getLogger(ChallengeDetailMMFeederManager.class);
    
    /**
     * DAO to access marathon match data from the transactional database.
     */
    private final ChallengeDetailMMFeederDAO challengeDetailMMFeederDAO;

    /**
     * The jestClient field
     */
    private final JestClient jestClient;

    /**
     * Create ChallengeDetailMMFeederManager
     *
     * @param jestClient the jestClient to use
     * @param challengeDetailMMFeederDAO the challengeDetailMMFeederDAO to use
     */
    public ChallengeDetailMMFeederManager(JestClient jestClient, ChallengeDetailMMFeederDAO challengeDetailMMFeederDAO) {
        this.jestClient = jestClient;
        this.challengeDetailMMFeederDAO = challengeDetailMMFeederDAO;
    }

    /**
     * Push marathon match data into challenge model in elasticsearch.
     *
     * @param param the param to use
     * @throws SupplyException if any error occurs
     */
    public void pushMarathonMatchDataIntoChallengeDetail(MmFeederParam param) throws SupplyException {
        logger.info("Enter of pushMarathonMatchDataIntoChallengeDetail");
        DataScienceHelper.checkMarathonFeederParam(param, "challenges");

        FilterParameter filter = new FilterParameter("roundIds=in(" + ChallengeFeederUtil.listAsString(param.getRoundIds()) + ")");
        QueryParameter queryParameter = new QueryParameter(new FieldSelector());
        queryParameter.setFilter(filter);
        List<ChallengeDetailData> mms = this.challengeDetailMMFeederDAO.getMarathonMatchesForChallengeDetails(queryParameter);

        DataScienceHelper.checkMissedIds(param, mms);
        
        List<SubmissionData> submissions = this.challengeDetailMMFeederDAO.getSubmissionsForChallengeDetail(queryParameter);
        associateAllSubmissions(mms, submissions);
        
        List<RegistrantData> registrants = this.challengeDetailMMFeederDAO.getRegistrants(queryParameter);
        associateAllRegistrants(mms, registrants);
        
        try {
            JestClientUtils.pushFeeders(jestClient, param, mms);
        } catch (IOException ioe) {
            SupplyException se = new SupplyException("Internal server error occurs", ioe);
            se.setStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw se;
        }
    }
    
    /**
     * Associate all submissions
     *
     * @param challenges the challenges to use
     * @param submissions the submissions to use
     */
    private static void associateAllSubmissions(List<ChallengeDetailData> challenges, List<SubmissionData> submissions) {
        for (SubmissionData item : submissions) {
            for (ChallengeDetailData challenge : challenges) {
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
     * Associate all resources
     *
     * @param challenges the challenges to use
     * @param resources the resources to use
     */
    private static void associateAllRegistrants(List<ChallengeDetailData> challenges, List<RegistrantData> registrants) {
        for (RegistrantData item : registrants) {
            for (ChallengeDetailData challenge : challenges) {
                if (challenge.getId().equals(item.getChallengeId())) {
                    if (challenge.getRegistrants() == null) {
                        challenge.setRegistrants(new ArrayList<RegistrantData>());
                    }
                    Integer rating = null;
                    if (item.getRating() != null) {
                        if (!item.getRating().equalsIgnoreCase("n/a")) {
                            rating = Integer.parseInt(item.getRating());
                        }
                    }
                    item.setColorStyle(getColorStyle(rating));
                    challenge.getRegistrants().add(item);
                    
                    break;
                }
            }
        }
        for (ResourceData item : registrants) {
            item.setChallengeId(null);
        }
    }
    
    /**
     * Get color style
     *
     * @param rating the rating to use
     * @return the String result representing the color
     */
    private static String getColorStyle(Integer rating) {
 
        if (rating == null) {
            return "color: #000000";
        }
 
        if (rating < 0) {
            return "color: #FF9900"; // orange
        }
        if (rating < 900) {
            return "color: #999999";// gray
        }
        if (rating < 1200) {
            return "color: #00A900";// green
        }
        if (rating < 1500) {
            return "color: #6666FF";// blue
        }
        if (rating < 2200) {
            return "color: #DDCC00";// yellow
        }
        if (rating > 2199) {
            return "color: #EE0000";// red
        }
        // return black otherwise.
        return "color: #000000";
 
    }
    
    /**
     * Get current timestamp from the database.
     *
     * @throws SupplyException if any error occurs
     * @return the timestamp result
     */
    public Date getTimestamp() throws SupplyException {
        return this.challengeDetailMMFeederDAO.getTimestamp().getDate();
    }

    /**
     * Get the marathon matches whose registration phase started after the specified date and after the last run timestamp.
     *
     * @param date The date param.
     * @param lastRunTimestamp The last run timestamp.
     * @return The list of TCID.
     */
    public List<TCID> getMatchesWithRegistrationPhaseStartedIds(java.sql.Date date, long lastRunTimestamp) {
        return this.challengeDetailMMFeederDAO.getMatchesWithRegistrationPhaseStartedIds(date, lastRunTimestamp);
    }
}
