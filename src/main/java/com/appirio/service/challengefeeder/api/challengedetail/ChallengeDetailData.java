/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api.challengedetail;

import java.util.List;

import com.appirio.service.challengefeeder.api.IdentifiableData;
import com.appirio.service.challengefeeder.api.TermsOfUseData;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the ChallengeDetailData model 
 * 
 * It's added in Topcoder ElasticSearch Feeder Service - Way To Populate Challenge-Detail Index For Legacy Marathon Matches v1.0
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
public class ChallengeDetailData extends IdentifiableData {

    /**
     * The round1Introduction field
     */
    @Getter
    @Setter
    private String round1Introduction;


    /**
     * The round2Introduction field
     */
    @Getter
    @Setter
    private String round2Introduction;


    /**
     * The introduction field
     */
    @Getter
    @Setter
    private String introduction;


    /**
     * The detailedRequirements field
     */
    @Getter
    @Setter
    private String detailedRequirements;


    /**
     * The finalSubmissionGuidelines field
     */
    @Getter
    @Setter
    private String finalSubmissionGuidelines;


    /**
     * The document field
     */
    @Getter
    @Setter
    private List<DocumentData> document;
    
    /**
     * The registrants field
     */
    @Getter
    @Setter
    private List<RegistrantData> registrants;
    
    /**
     * The terms field
     */
    @Getter
    @Setter
    private List<TermsOfUseData> terms;
    
    /**
     * The checkpoints field
     */
    @Getter
    @Setter
    private List<CheckpointSubmissionData> checkpoints;
    
    /**
     * The submissions field
     */
    @Getter
    @Setter
    private List<SubmissionData> submissions;
    
    /**
     * The snippet field
     */
    @Getter
    @Setter
    private String snippet; 
}
