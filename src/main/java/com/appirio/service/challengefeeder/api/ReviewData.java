/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the ReviewData model 
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
public class ReviewData {
    /**
     * The challengeId field
     */
    @Getter
    @Setter
    @JsonIgnore
    private Long challengeId;
    
    /**
     * The score field
     */
    @Getter
    @Setter
    private Double score;

    /**
     * The submissionId field
     */
    @Getter
    @Setter
    private Long submissionId;

    /**
     * The reviewerId field
     */
    @Getter
    @Setter
    private Long reviewerId;

    /**
     * The submitterHandle field
     */
    @Getter
    @Setter
    private String submitterHandle;

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
     * The reviewId field
     */
    @Getter
    @Setter
    private Long reviewId;

    /**
     * The reviewerHandle field
     */
    @Getter
    @Setter
    private String reviewerHandle;
}
