/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api.detail;

import com.appirio.service.challengefeeder.helper.CustomDateDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
     * The challenge Id field
     */
    @Getter
    @Setter
    private transient Long challengeId;

    /**
     * The submissionTime field
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @Getter
    @Setter
    private Date submissionTime;

    public BaseSubmissionData() {}

    public BaseSubmissionData(Long challengeId, Long submissionId, String submitter, Date submissionTime) {
        this.challengeId = challengeId;
        this.submissionId = submissionId;
        this.submitter = submitter;
        this.submissionTime = submissionTime;
    }
}
