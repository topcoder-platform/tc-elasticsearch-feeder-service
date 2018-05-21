/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api;

import java.util.Date;

import com.appirio.service.challengefeeder.helper.CustomDateDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;


/**
 * Represents the PhaseData model 
 *
 * Version 1.1 - Topcoder ElasticSearch Feeder Service - Way To Populate Challenge-Listing Index v1.0
 * - change the fields name from 'phaseStatus' and 'phaseType' to 'status' and 'type'
 *
 * @author TCCoder
 * @version 1.1 
 *
 */
public class PhaseData {
    /**
     * The challengeId field
     */
    @Getter
    @Setter
    @JsonIgnore
    private Long challengeId;
    
    /**
     * The phaseId field
     */
    @Getter
    @Setter
    private Long phaseId;

    /**
     * The status field
     */
    @Getter
    @Setter
    private String status;

    /**
     * The type field
     */
    @Getter
    @Setter
    private String type;

    /**
     * The duration field
     */
    @Getter
    @Setter
    private Long duration;

    /**
     * The scheduledStartTime field
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @Getter
    @Setter
    private Date scheduledStartTime;
    
    /**
     * The scheduledEndTime field
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @Getter
    @Setter
    private Date scheduledEndTime;
    
    /**
     * The actualStartTime field
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @Getter
    @Setter
    private Date actualStartTime;
    
    /**
     * The actualEndTime field
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @Getter
    @Setter
    private Date actualEndTime;

    /**
     * The fixedStartTime field
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @Getter
    @Setter
    private Date fixedStartTime;
    
    
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
}
