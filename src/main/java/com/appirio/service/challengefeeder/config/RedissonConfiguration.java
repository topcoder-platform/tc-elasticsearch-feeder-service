/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.config;

import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * It's used to hold the reisson related configuration.
 * 
 * It's added in Topcoder - Create CronJob For Populating Changed Challenges To Elasticsearch v1.0
 * 
 * <p>
 * Changes in v1.1 (Topcoder - Create CronJob For Populating Marathon Matches and SRMs To Elasticsearch v1.0):
 * <ul>
 * <li>Added reference to job MarathonMatchesJob.</li>
 * <li>Added reference to job SingleRoundMatchesJob.</li>
 * </ul>
 * </p>
 * <p>
 * Version 1.2 - Topcoder ElasticSearch Feeder Service - Way To Populate Challenge-Listing Index v1.0
 * <ul>
 * <li>add index/type for challenge listing</li>
 * </ul>
 * </p>
 * <p>
 * Version 1.3 - Topcoder ElasticSearch Feeder Service - Way To Populate Challenge-Listing Index For Legacy Marathon Matches v1.0
 * <ul>
 * <li>Add more configurations for the legacy marathon match loaded to the challenge list</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Version 1.4 - Topcoder ElasticSearch Feeder Service - Way To Populate Challenge-Detail Index For Legacy Marathon Matches v1.0
 * <ul>
 * <li>add more configurations for LoadChangedMMChallengeDetailJob.</li>
 * </ul> 
 * </p>
 *
 * <p>
 * Change in v1.5 (Topcoder ElasticSearch Feeder Service - Way To Populate Challenge-Detail Index):
 * <ul>
 *     <li>Added {@link #challengesDetailType}</li>
 *     <li>Added {@link #loadChangedChallengesDetailJobLastRunTimestampPrefix}</li>
 *     <li>Added {@link #loadChangedChallengesDetailJobLockerKeyName}</li>
 * </ul>
 * </p>
 * @author TCCoder
 * @version 1.5
 */
public class RedissonConfiguration {
    
    /**
     * The forceInitialLoad field
     */
    @JsonProperty
    @Getter
    @Setter
    private boolean forceInitialLoad;
    
    /**
     * The challenge index field
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String challengesIndex;

    /**
     * The challenge index listing field
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String challengesListingIndex;
    
    /**
     * The marathon match index field
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String mmIndex;
    
    /**
     * The challenge detail index field
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String challengesDetailIndex;
    
    /**
     * The single round match index field
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String srmsIndex;
    
    /**
     * The challenges type field
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String challengesType;

    /**
     * The challenges listing type field
     * @since 1.2
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String challengesDetailType;
    
    /**
     * The marahon match type field
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String mmType;
    
    /**
     * The single round match type field
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String srmsType;
    
    /**
     * The challenge type listing field
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String challengesListingType;
    
    /**
     * Represents the batch update size attribute.
     */
    @JsonProperty
    @Getter
    @Setter
    private int batchUpdateSize;
    
    /**
     * Represents the last run timestamp prefix attribute for job LoadChangedChallengesListingJob.
     * @since 1.2
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String loadChangedChallengesDetailJobLastRunTimestampPrefix;

    /**
     * Represents the last run timestamp prefix attribute for job LoadChangedChallengesJob.
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String loadChangedChallengesJobLastRunTimestampPrefix;
    
    /**
     * Represents the last run timestamp prefix attribute for job LoadChangedChallengesListingJob.
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String loadChangedChallengesListingJobLastRunTimestampPrefix;
    
    /**
     * Represents the last run timestamp prefix attribute for job MarathonMatchesJob.
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String marathonMatchesJobLastRunTimestampPrefix;

    /**
    * Represents the last run timestamp prefix attribute for LegacyMMToChallengeJob
    */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String legacyMMJobLastRunTimestampPrefix;

    /**
     * Represents the last run timestamp prefix attribute for job SingleRoundMatchesJob.
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String singleRoundMatchesJobLastRunTimestampPrefix;
    
    /**
     * Represents the last run timestamp prefix for LegacyMMToChallengeListJob
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String legacyMMToChallengeListingJobLastRunTimestampPrefix;

    /**
     * Represents the last run timestamp prefix attribute for load changed mm challenge detail job .
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String loadChangedMMChallengeDetailJobLastRunTimestampPrefix;
    
    /**
     * Represents the locker key name attribute for load changed mm challenge detail job.
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String loadChangedMMChallengeDetailJobLockerKeyName;
    
    /**
     * The cluster enabled field
     */
    @JsonProperty
    @Getter
    @Setter
    private boolean clusterEnabled;
    
    /**
     * Represents the locker key name attribute for job LoadChangedChallengesJob.
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String loadChangedChallengesJobLockerKeyName;

    /**
     * Represents the locker key name attribute for job LoadChangedChallengesListingJob.
     * @since 1.2
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String loadChangedChallengesDetailJobLockerKeyName;

    
    /**
     * Represents the locker key name attribute for job LoadChangedChallengesListingJob.
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String loadChangedChallengesListingJobLockerKeyName;
    
    /**
     * Represents the locker key name attribute for job MarathonMatchesJob.
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String marathonMatchesJobLockerKeyName;

    /**
     * Represents the locker key name attribute for LegacyMMToChallengeJob
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String legacyMMJobLockerKeyName;
    
    /**
     * Represents the locker key name attribute for LegacyMMToChallengeListJob
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String legacyMMToChallengeListingJobLockerKeyName;

    /**
     * Represents the locker key name attribute for job SingleRoundMatchesJob.
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String singleRoundMatchesJobLockerKeyName;
    
    /**
     * Represents the lock watchdog timeout attribute.
     */
    @JsonProperty
    @Getter
    @Setter
    private long lockWatchdogTimeout;
    
    /**
     * Represents the addresses attribute.
     */
    @JsonProperty
    @Getter
    @Setter
    private List<String> nodeAddresses;
    
    /**
     * Represents the single server adresses attribute.
     */
    @JsonProperty
    @Getter
    @Setter
    private String singleServerAddress;
    
    /**
     * The days to subtract in MarathonMatchesJob.
     */
    @JsonProperty
    @Getter
    @Setter
    private int marathonMatchesDaysToSubtract;
    
    /**
     * The days to subtract in SingleRoundMatchesJob.
     */
    @JsonProperty
    @Getter
    @Setter
    private int singleRoundMatchesDaysToSubtract;
    
    /**
     * Represents the marathonMatchesForumUrl.
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String marathonMatchesForumUrl;
    
}