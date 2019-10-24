/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api.detail;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Represents the RegistrantData model
 *
 * @author TCCoder
 * @version 1.0
 *
 */
public class RegistrantData {
    /**
     * The challengeId field
     */
    @Getter
    @Setter
    @JsonIgnore
    private transient Long challengeId;

    /**
     * The submissionDate field
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @Getter
    @Setter
    private Date submissionDate;

    /**
     * The reliability field
     */
    @Setter
    @Getter
    private Double reliability;

    /**
     * The handle field
     */
    @Getter
    @Setter
    private String handle;

    /**
     * The colorStyle field
     */
    @Getter
    @Setter
    private String colorStyle;

    /**
     * The rating field
     */
    @Getter
    @Setter
    private Integer rating;

    /**
     * The registrationDate field
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @Getter
    @Setter
    private Date registrationDate;
}
