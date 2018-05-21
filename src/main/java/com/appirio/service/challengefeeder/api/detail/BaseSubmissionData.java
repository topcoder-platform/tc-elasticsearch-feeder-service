/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api.detail;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Represents the BaseSubmissionData model
 *
 * @author TCCoder
 * @version 1.0
 *
 */
public class BaseSubmissionData {
    /**
     * The submissionId field
     */
    @Getter
    @Setter
    private Long submissionId;

    /**
     * The submitter field
     */
    @Getter
    @Setter
    private String submitter;

    /**
     * The submissionTime field
     */
    @Getter
    @Setter
    private Date submissionTime;
}
