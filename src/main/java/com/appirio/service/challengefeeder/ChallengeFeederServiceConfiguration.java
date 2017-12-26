/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder;

import com.appirio.service.BaseAppConfiguration;
import com.appirio.service.challengefeeder.config.JestClientConfiguration;
import com.appirio.service.supply.resources.SupplyDatasourceFactory;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * ChallengeFeederConfiguration for the supply server
 *
 * @author TCSCODER
 * @version 1.0
 */
public class ChallengeFeederServiceConfiguration extends BaseAppConfiguration {

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
}
