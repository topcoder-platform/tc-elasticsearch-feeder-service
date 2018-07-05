/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.config;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * The ChallengeConfiguration is for configuration.
 * 
 * It's added in Topcoder ElasticSearch Feeder Service - Way To Populate Challenge-Listing Index v1.0
 * 
 * @author TCCoder
 * @version 1.0
 */
public class ChallengeConfiguration {
    /**
     * Represents the design forum url prefix attribute.
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String designForumUrlPrefix;
    
    /**
     * Represents the studio forums url prefix attribute.
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String studioForumsUrlPrefix;
    
    /**
     * Represents the develop forums url prefix attribute.
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String developForumsUrlPrefix;
    
    /**
     * Represents the direct project link attribute.
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String directProjectLink;
}