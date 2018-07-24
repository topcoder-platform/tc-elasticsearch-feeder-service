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
    @Getter
    @Setter
    private Long submitterId;

    @Getter
    @Setter
    private String submitter;

    @Getter
    @Setter
    private List<SubmissionData> submissions;
}
