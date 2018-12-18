package com.appirio.service.challengefeeder.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

/**
 * The JobsConfiguration is for the jobs configuration
 * 
 * It's added in Topcoder Elasticsearch Feeder Service - Jobs Cleanup And Improvement v1.0
 * 
 * * Version 1.1 - TC Elasticsearch feeder - Add Job For populating rounds index v1.0
 * - add the RoundsJobConfiguration
 * 
 * 
 * @author TCCoder
 * @version 1.1 
 *
 */
public class JobsConfiguration { 
    /**
     * Represents the redissonConfiguration field
     */
    @JsonProperty("redissonConfiguration")
    @Getter
    private RedissonConfiguration redissonConfiguration;
    
    /**
     * The loadChangedChallengesListingJob field 
     */
    @JsonProperty("loadChangedChallengesListingJob")
    @Getter
    private JobConfiguration loadChangedChallengesListingJob;
    
    /**
     * The loadChangedChallengesDetailJob field 
     */
    @JsonProperty("loadChangedChallengesDetailJob")
    @Getter
    private JobConfiguration loadChangedChallengesDetailJob;
    
    /**
     * The legacyMMToChallengeListingJob field 
     */
    @JsonProperty("legacyMMToChallengeListingJob")
    @Getter
    private MMJobConfiguration legacyMMToChallengeListingJob;
    
    /**
     * The loadChangedMMChallengeDetailJob field 
     */
    @JsonProperty("loadChangedMMChallengeDetailJob")
    @Getter
    private MMJobConfiguration loadChangedMMChallengeDetailJob;
    
    /**
     * The marathonMatchesJob field 
     */
    @JsonProperty("marathonMatchesJob")
    @Getter
    private MMJobConfiguration marathonMatchesJob;
    
    /**
     * The singleRoundMatchesJob field 
     */
    @JsonProperty("singleRoundMatchesJob")
    @Getter
    private SRMJobConfiguration singleRoundMatchesJob;
    
    /**
     * The roundsJob field 
     */
    @JsonProperty("roundsJob")
    @Getter
    private RoundsJobConfiguration roundsJob;

}
