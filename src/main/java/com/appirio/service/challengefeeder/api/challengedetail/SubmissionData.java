/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api.challengedetail;

import java.util.Date;

import com.appirio.service.challengefeeder.helper.CustomDateDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the SubmissionData model 
 * 
 * It's added in Topcoder ElasticSearch Feeder Service - Way To Populate Challenge-Detail Index For Legacy Marathon Matches v1.0
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
public class SubmissionData {
    /**
     * The placement field.
     */
    @Getter
    @Setter
    private Integer placement;

    /**
     * The submitter field.
     */
    @Getter
    @Setter
    private String submitter;


    /**
     * The screeningScore field.
     */
    @Getter
    @Setter
    private Double screeningScore;


    /**
     * The initialScore field.
     */
    @Getter
    @Setter
    private Double initialScore;


    /**
     * The finalScore field.
     */
    @Getter
    @Setter
    private Double finalScore;


    /**
     * The submissionStatus field.
     */
    @Getter
    @Setter
    private String submissionStatus;


    /**
     * The submissionDate field.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @Getter
    @Setter
    private Date submissionTime;


    /**
     * The submissionId field.
     */
    @Getter
    @Setter
    private Long submissionId;
    
    /**
     * The challengeId field.
     */
    @Getter
    @Setter
    private Long challengeId;
    
}
