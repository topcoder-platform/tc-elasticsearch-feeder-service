/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api.detail;

import com.appirio.service.challengefeeder.api.IdentifiableData;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Represents the ChallengeDetailData model
 *
 * @author TCCoder
 * @version 1.0
 *
 */
public class ChallengeDetailData extends IdentifiableData {

    /**
     * The submission field
     */
    @Getter
    @Setter
    private List<SubmissionData> submissions;

    /**
     * The detailRequirement field
     */
    @Getter
    @Setter
    private String detailRequirements;

    /**
     * The finalSubmissionGuidelines fields
     */
    @Getter
    @Setter
    private String finalSubmissionGuidelines;

    /**
     * The documents field
     */
    @Getter
    @Setter
    private List<DocumentData> documents;

    /**
     * The registrants field
     */
    @Getter
    @Setter
    private List<RegistrantData> registrants;

    /**
     * The terms field
     */
    @Getter
    @Setter
    private List<TermsOfUseData> terms;

    /**
     * The checkpoints field
     */
    @Getter
    @Setter
    private List<BaseSubmissionData> checkpoints;

    /**
     * The snippet field
     */
    @Getter
    @Setter
    private String snippet;

    /**
     * The introduction field
     */
    @Setter
    @Getter
    private String introduction;

    /**
     * The round1Introduction field
     */
    @Getter
    @Setter
    private transient String round1Introduction;

    /**
     * The round2Introduction field
     */
    @Getter
    @Setter
    private transient String round2Introduction;

    /**
     * The studioDetailRequirements field
     */
    @Getter
    @Setter
    private transient String studioDetailRequirements;

    /**
     * The softwareDetailRequirements field
     */
    @Getter
    @Setter
    private transient String softwareDetailRequirements;

    /**
     * The type field
     */
    @Getter
    @Setter
    private transient Long type;
}
