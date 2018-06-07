/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
import com.appirio.service.challengefeeder.api.challengelisting.ChallengeListingData;

/**
 * ChallengeFeederUtil provides common methods such as associating the challenge data.
 * 
 * It's added in Topcoder - Populate Marathon Match Related Data Into Challenge Model In Elasticsearch v1.0
 *
 * Version 1.1 - Topcoder - Elasticsearch Service - Populate Challenge Points Prize In Challenges Index
 * 	- add the methods to associate the prize points with the challenge ids
 *
 * Version 1.2 - Topcoder ElasticSearch Feeder Service - Way To Populate Challenge-Detail Index
 *  - added {@link #getColorStyle(Integer)}
 *
 * @author TCSCODER
 * @version 1.2
 */
public class ChallengeFeederUtil {
    /**
     * Associate all checkpointPrizes
     *
     * @param challenges the challenges to use
     * @param checkpointPrizes the checkpointPrizes to use
     */
    static void associateAllCheckpointPrizes(List<ChallengeData> challenges, List<CheckpointPrizeData> checkpointPrizes) {
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
     * Associate all events
     *
     * @param challenges the challenges to use
     * @param events the events to use
     */
    static void associateAllEvents(List<ChallengeData> challenges, List<EventData> events) {
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
     * Associate all fileTypes
     *
     * @param challenges the challenges to use
     * @param fileTypes the fileTypes to use
     */
    static void associateAllFileTypes(List<ChallengeData> challenges, List<FileTypeData> fileTypes) {
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
     * Associate all phases
     *
     * @param challenges the challenges to use
     * @param allPhases the allPhases to use
     */
    static void associateAllPhases(List<ChallengeData> challenges, List<PhaseData> allPhases) {
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
     * Associate all prizes
     *
     * @param challenges the challenges to use
     * @param prizes the prizes to use
     */
    static void associateAllPrizes(List<ChallengeData> challenges, List<PrizeData> prizes) {
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
     * Associate all properties
     *
     * @param challenges the challenges to use
     * @param properties the properties to use
     */
    static void associateAllProperties(List<ChallengeData> challenges, List<PropertyData> properties) {
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
     * Associate all resources
     *
     * @param challenges the challenges to use
     * @param resources the resources to use
     */
    static void associateAllResources(List<ChallengeData> challenges, List<ResourceData> resources) {
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

    /**
     * Associate all reviews
     *
     * @param challenges the challenges to use
     * @param reviews the reviews to use
     */
    static void associateAllReviews(List<ChallengeData> challenges, List<ReviewData> reviews) {
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
     * Associate all submissions
     *
     * @param challenges the challenges to use
     * @param submissions the submissions to use
     */
    static void associateAllSubmissions(List<ChallengeData> challenges, List<SubmissionData> submissions) {
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
     * Associate all terms of use
     *
     * @param challenges the challenges to use
     * @param termsOfUse the termsOfUse to use
     */
    static void associateAllTermsOfUse(List<ChallengeData> challenges, List<TermsOfUseData> termsOfUse) {
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
     * Associate all winners
     *
     * @param challenges the challenges to use
     * @param winners the winners to use
     */
    static void associateAllWinners(List<ChallengeData> challenges, List<WinnerData> winners) {
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
     * List the items as string
     *
     * @param items the items to use
     * @return the String result, separated by comma
     */
    static String listAsString(List<? extends Object> items) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < items.size(); ++i) {
            res.append(items.get(i).toString());
            if (i < items.size() - 1) {
                res.append(", ");
            }
        }

        return res.toString();
    }
    
    /**
     * Associate all pointPrizes
     *
     * @param challenges the challenges to use
     * @param checkpointPrizes the checkpointPrizes to use
     */
    static void associateAllPointsPrize(List<ChallengeData> challenges, List<PrizeData> pointPrizes) {
        for (PrizeData item : pointPrizes) {
            for (ChallengeData challenge : challenges) {
                if (challenge.getId().equals(item.getChallengeId())) {
                    if (challenge.getPointPrizes() == null) {
                        challenge.setPointPrizes(new ArrayList<PrizeData>());
                    }
                    challenge.getPointPrizes().add(item);
                    break;
                }
            }
        }
        for (PrizeData item : pointPrizes) {
            item.setChallengeId(null);
        }
    }

    /**
     * Associate list of submitter for challenges
     *
     * @param challenges challenges
     * @param submitterIds submitterIds
     */
    static void associateSubmitterIds(List<ChallengeListingData> challenges, List<Map<String, Object>> submitterIds) {
        for (Map<String, Object> submitter : submitterIds) {
            for (ChallengeListingData challenge : challenges) {
                if (challenge.getChallengeId().equals(Long.valueOf(submitter.get("challengeId").toString()))) {
                    if (challenge.getSubmitterIds() == null) {
                        challenge.setSubmitterIds(new HashSet<>());
                    }
                    challenge.getSubmitterIds().add(Long.valueOf(submitter.get("submitterId").toString()));
                    break;
                }
            }
        }
    }
    /**
     * Get color style
     *
     * @param rating the rating to use
     * @return the String result representing the color
     * @since 1.2
     */
    public static String getColorStyle(Integer rating) {

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
}
