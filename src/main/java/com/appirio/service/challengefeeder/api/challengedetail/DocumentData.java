/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api.challengedetail;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the DocumentData model
 * 
 * It's added in Topcoder ElasticSearch Feeder Service - Way To Populate Challenge-Detail Index For Legacy Marathon Matches v1.0
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
public class DocumentData {
    /**
     * The documentName field.
     */
    @Getter
    @Setter
    private String documentName;

    /**
     * The url field.
     */
    @Getter
    @Setter
    private String url;
    
    /**
     * The challengeId field.
     */
    @Getter
    @Setter
    private Long challengeId;
}
