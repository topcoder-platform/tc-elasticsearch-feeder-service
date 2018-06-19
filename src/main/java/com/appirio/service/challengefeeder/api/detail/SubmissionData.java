/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api.detail;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the SubmissionData model 
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
public class SubmissionData extends BaseSubmissionData {
    /**
     * The placement field
     */
    @Getter
    @Setter
    private Integer placement;

    /**
     * The screeningScore field
     */
    @Getter
    @Setter
    private Double screeningScore;

    /**
     * The initialScore field
     */
    @Getter
    @Setter
    private Double initialScore;

    /**
     * The finalScore field
     */
    @Getter
    @Setter
    private Double finalScore;


    /**
     * The challengeId field
     */
    @Getter
    @Setter
    @JsonIgnore
    private transient Long challengeId;
    
    /**
     * The submissionType field
     */
    @Getter
    @Setter
    private transient Long submissionTypeId;

    /**
     * The submitterId field
     */
    @Getter
    @Setter
    private Long submitterId;

    /**
     * The submissionStatus field
     */
    @Getter
    @Setter
    private String submissionStatus;

    /**
     * The submissionImage field
     */
    @Getter
    @Setter
    private SubmissionImage submissionImage;
}
