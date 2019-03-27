/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api.challengelisting;

import com.appirio.service.challengefeeder.api.FileTypeData;
import com.appirio.service.challengefeeder.api.IdentifiableData;
import com.appirio.service.challengefeeder.api.PhaseData;
import com.appirio.service.challengefeeder.helper.CustomDateDeserializer;
import com.appirio.supply.constants.SubTrack;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Represents the ChallengeListData model 
 *
 * It's added in Topcoder ElasticSearch Feeder Service - Way To Populate Challenge-Listing Index v1.0
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
public class ChallengeListingData extends IdentifiableData {
    /**
     * The updatedAt field
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @Getter
    @Setter
    private Date updatedAt;

    /**
     * The createdAt field
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @Getter
    @Setter
    private Date createdAt;

    /**
     * The createdBy field
     */
    @Getter
    @Setter
    private String createdBy;

    /**
     * The updatedBy field
     */
    @Getter
    @Setter
    private String updatedBy;

    /**
     * The technologies field
     */
    @Getter
    @Setter
    private List<String> technologies;

    /**
     * The status field
     */
    @Getter
    private String status;

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
     * The challengeTitle field
     */
    @Getter
    @Setter
    private String challengeTitle;

    /**
     * The reviewType field
     */
    @Getter
    @Setter
    private String reviewType;

    /**
     * The forumId field
     */
    @Getter
    @Setter
    private Long forumId;

    /**
     * The numberOfSubmissions field
     */
    @Getter
    @Setter
    private Integer numberOfSubmissions;

    /**
     * The numberOfSubmitters field
     */
    @Getter
    @Setter
    private Integer numberOfSubmitters;

    /**
     * The numberOfRegistrants field
     */
    @Getter
    @Setter
    private Integer numberOfRegistrants;

    /**
     * The registrationStartDate field
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @Getter
    @Setter
    private Date registrationStartDate;

    /**
     * The registrationEndDate field
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @Getter
    @Setter
    private Date registrationEndDate;

    /**
     * The checkpointSubmissionEndDate field
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @Getter
    @Setter
    private Date checkpointSubmissionEndDate;

    /**
     * The submissionEndDate field
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @Getter
    @Setter
    private Date submissionEndDate;

    /**
     * The platforms field
     */
    @Getter
    @Setter
    private List<String> platforms;

    /**
     * The numberOfCheckpointPrizes field
     */
    @Getter
    @Setter
    private Integer numberOfCheckpointPrizes;

    /**
     * The totalCheckpointPrize field
     */
    @Getter
    @Setter
    private Double totalCheckpointPrize;

    /**
     * The totalPrize field
     */
    @Getter
    @Setter
    private Double totalPrize;

    /**
     * The isPrivate field
     */
    @Getter
    @Setter
    private Boolean isPrivate;

    /**
     * The submissionViewable field
     */
    @Getter
    @Setter
    private Boolean submissionViewable;

    /**
     * The winners field
     */
    @Getter
    @Setter
    private List<WinnerData> winners;

    /**
     * The checkpoint winners field
     */
    @Getter
    @Setter
    private List<WinnerData> checkpointWinners;

    /**
     * The events field
     */
    @Getter
    @Setter
    private List<EventData> events;

    /**
     * The drPoints field
     */
    @Getter
    @Setter
    private Double drPoints;

    /**
     * The reliabilityBonus field
     */
    @Getter
    @Setter
    private Double reliabilityBonus;

    /**
     * The isTask field
     */
    @Getter
    @Setter
    private Boolean isTask;

    /**
     * The environment field
     */
    @Getter
    @Setter
    private String environment;

    /**
     * The codeRepo field
     */
    @Getter
    @Setter
    private String codeRepo;

    /**
     * The groupIds field
     */
    @Getter
    @Setter
    private List<Long> groupIds;

    /**
     * The fileTypes field
     */
    @Getter
    @Setter
    private List<FileTypeData> fileTypes;

    /**
     * The screeningScorecardId field.
     */
    @Getter
    @Setter
    private Long screeningScorecardId;


    /**
     * The reviewScorecardId field.
     */
    @Getter
    @Setter
    private Long reviewScorecardId;


    /**
     * The cmcTaskId field.
     */
    @Getter
    @Setter
    private String cmcTaskId;


    /**
     * The topCheckPointPrize field.
     */
    @Getter
    @Setter
    private Double topCheckPointPrize;


    /**
     * The postingDate field.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @Getter
    @Setter
    private Date postingDate;


    /**
     * The allowStockArt field.
     */
    @Getter
    @Setter
    private String allowStockArt;


    /**
     * The forumLink field.
     */
    @Getter
    @Setter
    private String forumLink;
    
    /**
     * The forumType field.
     */
    @Getter
    @Setter
    private String forumType;


    /**
     * The submissionLimit field.
     */
    @Getter
    @Setter
    private Integer submissionLimit;


    /**
     * The directUrl field.
     */
    @Getter
    @Setter
    private String directUrl;


    /**
     * The projectId field.
     */
    @Getter
    @Setter
    private Long projectId;


    /**
     * The projectName field.
     */
    @Getter
    @Setter
    private String projectName;
    
    /**
     * The upcomingPhase field.
     */
    @Getter
    @Setter
    private PhaseData upcomingPhase;


    /**
     * The currentPhases field.
     */
    @Getter
    @Setter
    private List<PhaseData> currentPhases;


    /**
     * The appealsEndDate field.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @Getter
    @Setter
    private Date appealsEndDate;


    /**
     * The finalFixEndDate field.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @Getter
    @Setter
    private Date finalFixEndDate;
    
    /**
     * The numberOfCheckpointSubmissions field.
     */
    @Getter
    @Setter
    private Integer numberOfCheckpointSubmissions;
    
    /**
     * The prizes field
     */
    @Getter
    @Setter
    private List<Double> prize;
    
    /**
     * The phases field
     */
    @Getter
    @Setter
    private List<PhaseData> phases;
    
    /**
     * The challengeId field
     */
    @Getter
    @Setter
    private Long challengeId;
    
    /**
     *  The userIds field
     */
    @Getter
    @Setter
    private List<Long> userIds;

    /**
     * The Point prizes field
     */
    @Getter
    @Setter
    private List<Double> pointPrizes;

    /**
     * Submitter ids
     */
    @Getter
    @Setter
    private Set<Long> submitterIds;

    /**
     * Setter for {@link #track}
     * @param track
     */
    public void setTrack(String track) {
        this.track = StringUtils.trim(track);
    }

    /**
     * Setter for {@link #subTrack}
     * @param subTrack
     */
    public void setSubTrack(String subTrack) {
        // the subTrack should be directly set, should not be changed between
        // enum description and name
        try {
            SubTrack subTrackE = SubTrack.getEnumFromDescription(subTrack);
            this.subTrack = subTrackE.getSubTrackName();
        } catch (IllegalArgumentException iae) {
            this.subTrack = subTrack;
        }
    }

    /**
     * Set subTrack from SubTrack Enum
     * @param subTrackE SubTrack item
     */
    public void setSubTrackFromEnum(SubTrack subTrackE) {
        this.subTrack = subTrackE.toString();
    }

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = StringUtils.trim(status);
	}
}
