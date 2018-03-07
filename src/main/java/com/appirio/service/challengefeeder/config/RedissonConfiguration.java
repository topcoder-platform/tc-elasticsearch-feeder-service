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
 * @author TCCoder
 * @version 1.0
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
     * The challenges type field
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String challengesType;
    
    /**
     * Represents the batch update size attribute.
     */
    @JsonProperty
    @Getter
    @Setter
    private int batchUpdateSize;
    
    /**
     * Represents the last run timestamp prefix attribute.
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String lastRunTimestampPrefix;
    
    /**
     * The cluster enabled field
     */
    @JsonProperty
    @Getter
    @Setter
    private boolean clusterEnabled;
    
    /**
     * Represents the locker key name attribute.
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String lockerKeyName;
    
    /**
     * Represents the use linux native epoll attribute.
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private boolean useLinuxNativeEpoll;
    
    /**
     * Represents the lock watchdog timeout attribute.
     */
    @JsonProperty
    @Getter
    @Setter
    private long lockWatchdogTimeout;
    
    /**
     * Represents the adresses attribute.
     */
    @JsonProperty
    @Getter
    @Setter
    private List<String> nodeAdresses;
    
    /**
     * Represents the single server adresses attribute.
     */
    @JsonProperty
    @Getter
    @Setter
    private String singleServerAddress;
    
}