/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api;

import com.appirio.service.challengefeeder.helper.CustomDateDeserializer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Represents the ResourceData model 
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
public class ResourceData {
    /**
     * The challengeId field
     */
    @Getter
    @Setter
    @JsonIgnore
    private Long challengeId;
    
    /**
     * The resourceId field
     */
    @Getter
    @Setter
    private Long resourceId;

    /**
     * The role field
     */
    @Getter
    @Setter
    private String role;

    /**
     * The projectPhaseId field
     */
    @Getter
    @Setter
    private Long projectPhaseId;

    /**
     * The reliability field
     */
    @Getter
    @Setter
    private String reliability;

    /**
     * The registrationDate field
     *
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @Getter
    @Setter
    private Date registrationDate;

    /**
     * The rating field
     */
    @Getter
    @Setter
    private String rating;

    /**
     * The handle field
     */
    @Getter
    @Setter
    private String handle;

    /**
     * The userId field
     */
    @Getter
    @Setter
    private Long userId;
}
