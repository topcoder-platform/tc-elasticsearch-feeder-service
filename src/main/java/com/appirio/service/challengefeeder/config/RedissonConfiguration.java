/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.config;

import java.util.List;

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
 * 
 * <p>
 * Version 2.0 - Topcoder Elasticsearch Feeder Service - Jobs Cleanup And Improvement v1.0
 * <ul>
 *     <li>keep only redisson connection configurations</li>
 * </ul>
 * </p>
 * @author TCCoder
 * @version 2.0
 */
public class RedissonConfiguration {
    /**
     * The cluster enabled field
     */
    @JsonProperty
    @Getter
    @Setter
    private boolean clusterEnabled;
    
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
    
}