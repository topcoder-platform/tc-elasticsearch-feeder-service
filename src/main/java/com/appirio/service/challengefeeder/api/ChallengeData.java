/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api;

import java.util.Date;
import java.util.List;

import com.appirio.service.challengefeeder.helper.CustomDateDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the ChallengeData model 
 * 
 * Version 1.1 - Topcoder - Populate Marathon Match Related Data Into Challenge Model In Elasticsearch v1.0
 * - It extends from IdentifiableData
 * 
 * 
 * @author TCCoder
 * @version 1.1 
 *
 */
public class ChallengeData extends IdentifiableData {
    /**
     * The numRegistrants field
     */
    @Getter
    @Setter
    private Long numRegistrants;

    /**
     * The registrationStartDate field
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @Getter
    @Setter
    private Date registrationStartDate;

    /**
     * The softwareFinalSubmissionGuidelines field
     */
    @Getter
    @Setter
    private String softwareFinalSubmissionGuidelines;

    /**
     * The softwareDetailRequirements field
     */
    @Getter
    @Setter
    private String softwareDetailRequirements;

    /**
     * The round2Introduction field
     */
    @Getter
    @Setter
    private String round2Introduction;

    /**
     * The marathonMatchRules field
     */
    @Getter
    @Setter
    private String marathonMatchRules;

    /**
     * The reviewType field
     */
    @Getter
    @Setter
    private String reviewType;

    /**
     * The isPrivate field
     */
    @Getter
    @Setter
    private Boolean isPrivate;

    /**
     * The round1Introduction field
     */
    @Getter
    @Setter
    private String round1Introduction;

    /**
     * The type field.
     * It's default to "challenges"
     */
    @Getter
    @Setter
    private String type = "challenges";

    /**
     * The numSubmissions field
     */
    @Getter
    @Setter
    private Long numSubmissions;

    /**
     * The totalPrize field
     */
    @Getter
    @Setter
    private Double totalPrize;

    /**
     * The platforms field
     */
    @Getter
    @Setter
    private String platforms;

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
     * The updatedAt field
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @Getter
    @Setter
    private Date updatedAt;
    
    /**
     * The updatedBy field
     */
    @Getter
    @Setter
    private String updatedBy;

    /**
     * The marathonMatchDetailRequirements field
     */
    @Getter
    @Setter
    private String marathonMatchDetailRequirements;

    /**
     * The registrationEndDate field
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @Getter
    @Setter
    private Date registrationEndDate;

    /**
     * The totalCheckpointPrize field
     */
    @Getter
    @Setter
    private Double totalCheckpointPrize;

    /**
     * The directProjectId field
     */
    @Getter
    @Setter
    private Long directProjectId;

    /**
     * The subTrack field
     */
    @Getter
    @Setter
    private String subTrack;

    /**
     * The track field
     */
    @Getter
    @Setter
    private String track;

    /**
     * The drPoints field
     */
    @Getter
    @Setter
    private Double drPoints;

    /**
     * The introduction field
     */
    @Getter
    @Setter
    private String introduction;

    /**
     * The copilotDetailRequirements field
     */
    @Getter
    @Setter
    private String copilotDetailRequirements;

    /**
     * The submissionEndDate field
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @Getter
    @Setter
    private Date submissionEndDate;

    /**
     * The checkpointSubmissionEndDate field
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @Getter
    @Setter
    private Date checkpointSubmissionEndDate;

    /**
     * The isTask field
     */
    @Getter
    @Setter
    private Boolean isTask;

    /**
     * The reliabilityBonus field
     */
    @Getter
    @Setter
    private Double reliabilityBonus;

    /**
     * The submissionViewable field
     */
    @Getter
    @Setter
    private Boolean submissionViewable;

    /**
     * The directProjectName field
     */
    @Getter
    @Setter
    private String directProjectName;

    /**
     * The numberOfCheckpointPrizes field
     */
    @Getter
    @Setter
    private Integer numberOfCheckpointPrizes;

    /**
     * The technologies field
     */
    @Getter
    @Setter
    private String technologies;

    /**
     * The environment field
     */
    @Getter
    @Setter
    private String environment;

    /**
     * The name field
     */
    @Getter
    @Setter
    private String name;

    /**
     * The codeRepo field
     */
    @Getter
    @Setter
    private String codeRepo;

    /**
     * The forumId field
     */
    @Getter
    @Setter
    private Long forumId;

    /**
     * The studioDetailRequirements field
     */
    @Getter
    @Setter
    private String studioDetailRequirements;

    /**
     * The status field
     */
    @Getter
    @Setter
    private String status;


    /**
     * The hasUserSubmittedForReview field
     */
    @Getter
    @Setter
    private List<String> hasUserSubmittedForReview;

    /**
     * The userIds field
     */
    @Getter
    @Setter
    private List<Long> userIds;
    
    /**
     * The groupIds field
     */
    @Getter
    @Setter
    private List<Long> groupIds;

    /**
     * The tags field
     */
    @Getter
    @Setter
    private List<String> tags;

    /**
     * The phases field
     */
    @Getter
    @Setter
    private List<PhaseData> phases;
    
    /**
     * The properties field
     */
    @Getter
    @Setter
    private List<PropertyData> properties;
    
    /**
     * The resources field
     */
    @Getter
    @Setter
    private List<ResourceData> resources;
    
    /**
     * The events field
     */
    @Getter
    @Setter
    private List<EventData> events;
    
    /**
     * The fileTypes field
     */
    @Getter
    @Setter
    private List<FileTypeData> fileTypes;
    
    /**
     * The prizes field
     */
    @Getter
    @Setter
    private List<PrizeData> prizes;
    
    /**
     * The checkpointPrizes field
     */
    @Getter
    @Setter
    private List<CheckpointPrizeData> checkpointPrizes;
    
    /**
     * The reviews field
     */
    @Getter
    @Setter
    private List<ReviewData> reviews;
    
    /**
     * The submissions field
     */
    @Getter
    @Setter
    private List<SubmissionData> submissions;
    
    /**
     * The terms field
     */
    @Getter
    @Setter
    private List<TermsOfUseData> terms;
    
    /**
     * The winners field
     */
    @Getter
    @Setter
    private List<WinnerData> winners;
}
