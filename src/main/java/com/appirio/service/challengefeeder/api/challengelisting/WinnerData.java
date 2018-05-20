/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api.challengelisting;

import java.util.Date;

import com.appirio.service.challengefeeder.helper.CustomDateDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the WinnerData entity.
 * 
 * It's added in Topcoder ElasticSearch Feeder Service - Way To Populate Challenge-Listing Index For Legacy Marathon Matches v1.0
 * 
 * @author TCCoder
 * @version 1.0
 */
public class WinnerData {
    /**
     * Represents the challenge id field.
     */
    @Getter
    @Setter
    private Long challengeId;
    
    /**
     * Represents the submission id field.
     */
    @Getter
    @Setter
    private long submissionId;
    
    /**
     * Represents the submitter field.
     */
    @Getter
    @Setter
    private String submitter;
    
    /**
     * Represents the submission time field.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @Getter
    @Setter
    private Date submissionTime;
    
    /**
     * Represents the rank field.
     */
    @Getter
    @Setter
    private String rank;
    
    /**
     * Represents the points field.
     */
    @Getter
    @Setter
    private double points;
    
}