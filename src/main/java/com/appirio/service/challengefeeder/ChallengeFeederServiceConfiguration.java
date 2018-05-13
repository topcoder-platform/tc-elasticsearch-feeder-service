/*
 * Copyright (C) 2017 - 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder;

import com.appirio.service.BaseAppConfiguration;
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
 * Version 1.2 - Topcoder ElasticSearch Feeder Service - Way To Populate Challenge-Listing Index
 * - add commonConfiguration
 * @author TCSCODER
 * @version 1.2
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
     * Getter commonConfiguration
     * @return
     */
    public CommonConfiguration getCommonConfiguration() {
        return commonConfiguration;
    }
}
