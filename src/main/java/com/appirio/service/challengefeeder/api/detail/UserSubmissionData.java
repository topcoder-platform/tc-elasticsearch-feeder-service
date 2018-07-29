/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api.detail;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * User Submissions
 */
public class UserSubmissionData {
    /**
     * submitterId field
     */
    @Getter
    @Setter
    private Long submitterId;

    /**
     * submitter field
     */
    @Getter
    @Setter
    private String submitter;

    /**
     * List of user submission
     */
    @Getter
    @Setter
    private List<SubmissionData> submissions;

    /**
     * user's rank
     */
    @Getter
    @Setter
    private Rank rank;

    /**
     * user's score
     */
    @Getter
    @Setter
    private Score score;
}
