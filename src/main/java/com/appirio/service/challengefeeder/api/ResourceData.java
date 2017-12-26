/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the ResourceData model 
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
public class ResourceData {
    /**
     * The challengeId field
     */
    @Getter
    @Setter
    @JsonIgnore
    private Long challengeId;
    
    /**
     * The resourceId field
     */
    @Getter
    @Setter
    private Long resourceId;

    /**
     * The role field
     */
    @Getter
    @Setter
    private String role;

    /**
     * The projectPhaseId field
     */
    @Getter
    @Setter
    private Long projectPhaseId;

    /**
     * The reliability field
     */
    @Getter
    @Setter
    private String reliability;

    /**
     * The registrationDate field
     * 
     * Use String type for this value as it's retrieved as String from database
     */
    @Getter
    @Setter
    private String registrationDate;

    /**
     * The rating field
     */
    @Getter
    @Setter
    private String rating;

    /**
     * The handle field
     */
    @Getter
    @Setter
    private String handle;

    /**
     * The userId field
     */
    @Getter
    @Setter
    private Long userId;
}
