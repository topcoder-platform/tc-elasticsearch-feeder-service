/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api;

import com.appirio.service.challengefeeder.helper.CustomDateDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


/**
 * Represents the base model for both Data Science sub tracks, Marathon and SRM.
 * <p>
 * Added in Topcoder - Add Endpoints To Populating Marathon Matches And SRMs Into Elasticsearch v1.0
 * </p>
 *
 * @author TCCoder
 * @version 1.0
 */
public abstract class DataScienceData {
    /**
     * The id field.
     */
    @Getter
    @Setter
    private Long id;

    /**
     * The name field.
     */
    @Getter
    @Setter
    private String name;

    /**
     * The status field.
     */
    @Getter
    @Setter
    private String status;

    /**
     * The startDate field
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @Getter
    @Setter
    private Date startDate;

    /**
     * The endDate field
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @Getter
    @Setter
    private Date endDate;

    /**
     * The track field.
     */
    @Getter
    @Setter
    private String track;

    /**
     * The sub-track field.
     */
    @Getter
    @Setter
    private String subTrack;

    /**
     * The forumId field.
     */
    @Getter
    @Setter
    private Long forumId;

    /**
     * The roundId field
     */
    @Getter
    @Setter
    private Long roundId;

    /**
     * The registrationStartAt field
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @Getter
    @Setter
    private Date registrationStartAt;

    /**
     * The registrationEndAt field
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @Getter
    @Setter
    private Date registrationEndAt;

    /**
     * The codingStartAt field
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @Getter
    @Setter
    private Date codingStartAt;

    /**
     * The codingEndAt field
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @Getter
    @Setter
    private Date codingEndAt;

    /**
     * The systemTestStartAt field
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @Getter
    @Setter
    private Date systemTestStartAt;

    /**
     * The systemTestEndAt field
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @Getter
    @Setter
    private Date systemTestEndAt;

    /**
     * The userIds field
     */
    @Getter
    @Setter
    private List<Long> userIds;

    /**
     * The numberOfRegistrants field
     */
    @Getter
    @Setter
    private Long numberOfRegistrants;
}
