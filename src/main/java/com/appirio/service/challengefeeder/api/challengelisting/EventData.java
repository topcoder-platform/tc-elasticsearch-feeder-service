/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api.challengelisting;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the EventData entity.
 * 
 * It's added in Topcoder ElasticSearch Feeder Service - Way To Populate Challenge-Listing Index For Legacy Marathon Matches v1.0
 * 
 * @author TCCoder
 * @version 1.0
 */
public class EventData {
    /**
     * Represents the challenge id field.
     */
    @Getter
    @Setter
    private Long challengeId;
    
    /**
     * Represents the id field.
     */
    @Getter
    @Setter
    private long id;
    
    /**
     * Represents the event short desc field.
     */
    @Getter
    @Setter
    private String eventShortDesc;
    
    /**
     * Represents the event description field.
     */
    @Getter
    @Setter
    private String eventDescription;
    
}