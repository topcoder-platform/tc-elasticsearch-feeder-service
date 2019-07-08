/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api.detail;

import com.appirio.service.challengefeeder.api.IdentifiableData;
import com.appirio.supply.constants.SubTrack;
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
     * The track field
     */
    @Getter
    private String track;

    /**
     * The subTrack field
     */
    @Getter
    private String subTrack;

    /**
     * The submission field
     */
    @Getter
    @Setter
    private List<UserSubmissionData> submissions;

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
     * The marathonMatchDetailRequirements field
     */
    @Getter
    @Setter
    private transient String marathonMatchDetailRequirements;

    /**
     * The marathonMatchRules field
     */
    @Getter
    @Setter
    private transient String marathonMatchRules;

    @Getter
    @Setter
    private transient Boolean isSysTestCompleted;

    @Getter
    @Setter
    private transient Long numberOfCheckpointPhase;

    /**
     * Setter for {@link #track}
     * @param track
     */
    public void setTrack(String track) {
       this.track = track.trim();
    }

    /**
     * Setter for {@link #subTrack}
     * @param subTrack
     */
    public void setSubTrack(String subTrack) {
        SubTrack subTrackE = SubTrack.getEnumFromDescription(subTrack);
        this.subTrack = subTrackE.getSubTrackName();
    }

    /**
     * Set subTrack from SubTrack Enum
     * @param subTrackE SubTrack item
     */
    public void setSubTrackFromEnum(SubTrack subTrackE) {
        this.subTrack = subTrackE.toString();
    }
}
