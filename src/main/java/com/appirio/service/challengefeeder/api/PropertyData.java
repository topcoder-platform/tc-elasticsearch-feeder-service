/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the PropertyData model 
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
public class PropertyData {
    /**
     * The challengeId field
     */
    @Getter
    @Setter
    @JsonIgnore
    private Long challengeId;
    
    /**
     * The name field
     */
    @Getter
    @Setter
    private String name;

    /**
     * The propertyId field
     */
    @Getter
    @Setter
    private Long propertyId;

    /**
     * The value field
     */
    @Getter
    @Setter
    private String value;
}
