/*
<<<<<<< HEAD
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
=======
 * Copyright (C) 2017 - 2018 TopCoder Inc., All Rights Reserved.
>>>>>>> cb1aae67e0f2e8125747683d2b9bea5295b8ad7d
 */
package com.appirio.service.challengefeeder;

import com.appirio.service.BaseAppConfiguration;
import com.appirio.service.challengefeeder.config.ChallengeConfiguration;
import com.appirio.service.challengefeeder.config.CommonConfiguration;
import com.appirio.service.challengefeeder.config.JestClientConfiguration;
import com.appirio.service.challengefeeder.config.RedissonConfiguration;
import com.appirio.service.supply.resources.SupplyDatasourceFactory;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.spinscale.dropwizard.jobs.JobConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ChallengeFeederConfiguration for the supply server
 *
 * Version 1.1 - Topcoder - Create CronJob For Populating Changed Challenges To Elasticsearch v1.0
 * - add the cron job related configuration
 * 
 * Version 1.2 - Topcoder ElasticSearch Feeder Service - Way To Populate Challenge-Listing Index v1.0
 * - add the challenge configuration
 *
 * Version 1.3 - Topcoder ElasticSearch Feeder Service - Way To Populate Challenge-Listing Index
 * - add commonConfiguration
 * @author TCSCODER
 * @version 1.3
 */
public class ChallengeFeederServiceConfiguration extends BaseAppConfiguration implements JobConfiguration {

    /**
     * Datasources
     */
    @Valid
    @NotNull
    @JsonProperty
    private List<SupplyDatasourceFactory> databases = new ArrayList<>();
    
    /**
     * Jest client configuration
     */
    @Valid
    @NotNull
    @JsonProperty
    private JestClientConfiguration jestClientConfiguration = new JestClientConfiguration();

    /**
     * The jobs field
     */
    @JsonProperty("jobs")
    private Map<String , String> jobs;
    
    /**
     * Represents the redissonConfiguration field
     */
    @JsonProperty("redissonConfiguration")
    private RedissonConfiguration redissonConfiguration;
    
    /**
     * The challengeConfiguration
     */
    @Valid
    @NotNull
    @JsonProperty("challengeConfiguration")
    private final ChallengeConfiguration challengeConfiguration = new ChallengeConfiguration();

    /**
     * common configuration section
     */
    @Valid
    @NotNull
    @JsonProperty("commonConfiguration")
    private CommonConfiguration commonConfiguration;

    /**
     * Get the data source factory
     *
     * @return Data source factory
     */
    public List<SupplyDatasourceFactory> getDatabases() {
        return databases;
    }

    /**
     * Get the jest client configuration
     * 
     * @return the jest client configuration
     */
    public JestClientConfiguration getJestClientConfiguration() {
        return jestClientConfiguration;
    }

    /**
     * Get jobs
     *
     * @return the Map<String,String> result
     */
    public Map<String, String> getJobs() {
        return jobs;
    }

    /**
     * Get redissonConfiguration
     * @return the redissonConfiguration
     */
    public RedissonConfiguration getRedissonConfiguration() {
        return this.redissonConfiguration;
    }

    /**
     * Get challenge configuration
     *
     * @return the ChallengeConfiguration
     */
    public ChallengeConfiguration getChallengeConfiguration() {
        return this.challengeConfiguration;
    }

    /**
     * Getter commonConfiguration
     * @return
     */
    public CommonConfiguration getCommonConfiguration() {
        return commonConfiguration;
    }
}
