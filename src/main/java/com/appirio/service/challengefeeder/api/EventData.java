/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the EventData model 
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
public class EventData {
    /**
     * The challengeId field
     */
    @Getter
    @Setter
    @JsonIgnore
    private Long challengeId;
    
    /**
     * The eventId field
     */
    @Getter
    @Setter
    private Long eventId;

    /**
     * The eventName field
     */
    @Getter
    @Setter
    private String eventName;
}
