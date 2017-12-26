/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the WinnerData model 
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
public class WinnerData {
    /**
     * The challengeId field
     */
    @Getter
    @Setter
    @JsonIgnore
    private Long challengeId;
    
    /**
     * The photoURL field
     */
    @Getter
    @Setter
    private String photoURL;

    /**
     * The handle field
     */
    @Getter
    @Setter
    private String handle;

    /**
     * The placement field
     */
    @Getter
    @Setter
    private Integer placement;

    /**
     * The type field
     */
    @Getter
    @Setter
    private String type;

    /**
     * The userId field
     */
    @Getter
    @Setter
    private Long userId;
}
