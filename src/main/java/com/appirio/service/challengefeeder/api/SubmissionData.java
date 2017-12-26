/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api;

import java.util.Date;

import com.appirio.service.challengefeeder.helper.CustomDateDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the SubmissionData model 
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
public class SubmissionData {
    /**
     * The challengeId field
     */
    @Getter
    @Setter
    @JsonIgnore
    private Long challengeId;
    
    /**
     * The submissionType field
     */
    @Getter
    @Setter
    private String submissionType;

    /**
     * The submitter field
     */
    @Getter
    @Setter
    private String submitter;

    /**
     * The finalScore field
     */
    @Getter
    @Setter
    private Double finalScore;

    /**
     * The submissionId field
     */
    @Getter
    @Setter
    private Long submissionId;

    /**
     * The submitterId field
     */
    @Getter
    @Setter
    private Long submitterId;

    /**
     * The initialScore field
     */
    @Getter
    @Setter
    private Double initialScore;

    /**
     * The placement field
     */
    @Getter
    @Setter
    private Integer placement;

    /**
     * The submittedAt field
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @Getter
    @Setter
    private Date submittedAt;

    /**
     * The screeningScore field
     */
    @Getter
    @Setter
    private Double screeningScore;

    /**
     * The status field
     */
    @Getter
    @Setter
    private String status;
}
