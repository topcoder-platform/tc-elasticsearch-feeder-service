/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.config;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * The jest client configuration
 * 
 * @author TCSCODER
 * @version 1.0
 */
public class JestClientConfiguration {
    
    /**
     * The elastic search URL
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String elasticSearchUrl;
    
    /**
     * The challenges index name
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String challengesIndexName;

    /**
     * The max total connections
     */
    @JsonProperty
    @Getter
    @Setter
    private int maxTotalConnections;
    
    /**
     * The connection timeout in milli-seconds
     */
    @JsonProperty
    @Getter
    @Setter
    private int connTimeout;
    
    /**
     * The read timeout in milli-seconds
     */
    @JsonProperty
    @Getter
    @Setter
    private int readTimeout;
    
    /**
     * Connect to AWS ES or not
     */
    @JsonProperty
    @Getter
    @Setter
    private boolean awsSigningEnabled;
    
    /**
     * The AWS region
     */
    @JsonProperty
    @Getter
    @Setter
    private String awsRegion;
    
    /**
     * The AWS service
     */
    @JsonProperty
    @Getter
    @Setter
    private String awsService; 
    
    /**
     * Empty constructor
     */
    public JestClientConfiguration() {}
}
