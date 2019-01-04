/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.manager;

import com.appirio.service.challengefeeder.api.detail.ChallengeDetailData;
import com.appirio.service.challengefeeder.api.detail.MmResult;
import com.appirio.service.challengefeeder.api.detail.Rank;
import com.appirio.service.challengefeeder.api.detail.RegistrantData;
import com.appirio.service.challengefeeder.api.detail.Score;
import com.appirio.service.challengefeeder.api.detail.SubmissionData;
import com.appirio.service.challengefeeder.api.detail.UserSubmissionData;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        DataScienceHelper.checkMarathonFeederParam(param, "challenges");
        FilterParameter filter = new FilterParameter("roundIds=in(" + ChallengeFeederUtil.listAsString(param.getRoundIds()) + ")");
        QueryParameter queryParameter = new QueryParameter(new FieldSelector());
        queryParameter.setFilter(filter);
        List<ChallengeDetailData> mms = this.challengeDetailMMFeederDAO.getMarathonMatchesForChallengeDetails(queryParameter);

        List<Long> ids = mms.stream().map(c -> c.getId()).collect(Collectors.toList());
        List<Long> idsNotFound = param.getRoundIds().stream().filter(id -> !ids.contains(id)).collect(Collectors.toList());

        if (!idsNotFound.isEmpty()) {
            logger.warn("These challenge ids can not be found:" + idsNotFound);

            ids.removeAll(idsNotFound);
        }

        if (mms.size() == 0) {
            return;
        }
        
        List<SubmissionData> submissions = this.challengeDetailMMFeederDAO.getSubmissionsForChallengeDetail(queryParameter);
        List<MmResult> provisonalResult = this.challengeDetailMMFeederDAO.getMmProvisionalResult(queryParameter);
        associateAllSubmissions(mms, submissions, provisonalResult);
        
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
     * Associate all submissions and result
     *
     * @param challenges the challenges to use
     * @param submissions the submissions to use
     * @param provisionalResult the provisional result
     */
    private static void associateAllSubmissions(List<ChallengeDetailData> challenges, List<SubmissionData> submissions,
                                                List<MmResult> provisionalResult) {
        int provRank = 0;
        int provRankNoTie = 0;
        Double proveScore = 0.0;
        for (int i = 0; i < provisionalResult.size(); i++) {
            provRankNoTie++;
            if (!proveScore.equals(provisionalResult.get(i).getProvisionalScore())) {
                provRank = provRankNoTie;
            }
            proveScore = provisionalResult.get(i).getProvisionalScore();
            provisionalResult.get(i).setProvisionalRank(provRank);
        }

        Map<Long, Map<Long, List<MmResult>>> provisionalResultMap = provisionalResult.stream()
                .collect(Collectors.groupingBy(MmResult::getChallengeId,
                        Collectors.groupingBy(MmResult::getUserId)));

        Map<Long, Map<Long, List<SubmissionData>>> challengeSubmissions = submissions.stream()
                .collect(Collectors.groupingBy(SubmissionData::getChallengeId,
                        Collectors.groupingBy(SubmissionData::getSubmitterId)));

        challenges.forEach(c -> {
            if (challengeSubmissions.get(c.getId()) != null) {
                List<UserSubmissionData> userSubmissions = new ArrayList<>();
                for (Map.Entry<Long, List<SubmissionData>> entry : challengeSubmissions.get(c.getId()).entrySet()) {
                    UserSubmissionData userSubmissionData = new UserSubmissionData();
                    userSubmissionData.setSubmitterId(entry.getKey());
                    userSubmissionData.setSubmitter(entry.getValue().get(0).getSubmitter());
                    userSubmissionData.setSubmissions(entry.getValue());

                    MmResult provisionItem = null;
                    try {
                        provisionItem = provisionalResultMap.get(c.getId()).get(entry.getKey()).get(0);
                    } catch (Exception e) {
                        //do nothing
                    }

                    Rank rank = null;
                    Score score = null;
                    if (provisionItem != null) {
                        rank = new Rank();
                        score = new Score();
                        rank.setInterim(provisionItem.getProvisionalRank());
                        score.setProvisional(provisionItem.getProvisionalScore());
                        if (provisionItem.getFinalRank() != null && provisionItem.getFinalScore() !=null) {
                            rank.setFinalRank(provisionItem.getFinalRank());
                            score.setFinalScore(provisionItem.getFinalScore());
                        }
                    }

                    userSubmissionData.setRank(rank);
                    userSubmissionData.setScore(score);
                    userSubmissions.add(userSubmissionData);
                }

                userSubmissions.sort((UserSubmissionData usd1, UserSubmissionData usd2) -> {
                    Double r1;
                    Double r2;
                    if (c.getIsSysTestCompleted()) {
                        r1 = (usd1.getScore() != null && usd1.getScore().getFinalScore() != null) ? usd1.getScore().getFinalScore() : null;
                        r2 = (usd2.getScore() != null && usd2.getScore().getFinalScore() != null) ? usd2.getScore().getFinalScore() : null;
                    } else {
                        r1 = (usd1.getSubmissions().size() > 0) ? usd1.getSubmissions().get(0).getFinalScore() : null;
                        r2 = (usd2.getSubmissions().size() > 0) ? usd2.getSubmissions().get(0).getFinalScore() : null;
                    }
                    if (r1 == null) return 1;
                    if (r2 == null) return -1;
                    if (r1 > r2) {
                        return -1;
                    } else if (r2 > r1) {
                        return 1;
                    } else {
                        return 0;
                    }
                });
                c.setSubmissions(userSubmissions);
            }
        });
    }

    /**
     * Associate all resources
     *
     * @param challenges the challenges to use
     * @param registrants the resources to use
     */
    private static void associateAllRegistrants(List<ChallengeDetailData> challenges, List<RegistrantData> registrants) {
        for (RegistrantData item : registrants) {
            for (ChallengeDetailData challenge : challenges) {
                if (challenge.getId().equals(item.getChallengeId())) {
                    if (challenge.getRegistrants() == null) {
                        challenge.setRegistrants(new ArrayList<>());
                    }
                    item.setColorStyle(ChallengeFeederUtil.getColorStyle(item.getRating()));
                    challenge.getRegistrants().add(item);
                    break;
                }
            }
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
        return this.challengeDetailMMFeederDAO.getMatchesWithRegistrationPhaseStartedIds(date, lastRunTimestamp);
    }
}
