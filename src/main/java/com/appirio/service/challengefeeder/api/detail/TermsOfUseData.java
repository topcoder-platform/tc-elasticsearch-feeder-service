/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api.detail;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the TermsOfUseData model
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
public class TermsOfUseData {
    /**
     * The challengeId field
     */
    @Getter
    @Setter
    @JsonIgnore
    private transient Long challengeId;
    
    /**
     * The termsOfUseId field
     */
    @Getter
    @Setter
    private Long termsOfUseId;

    /**
     * The role field
     */
    @Getter
    @Setter
    private String role;

    /**
     * The agreeabilityType field
     */
    @Getter
    @Setter
    private String agreeabilityType;

    /**
     * The title field
     */
    @Getter
    @Setter
    private String title;

    /**
     * The templateId field
     */
    @Getter
    @Setter
    private String templateId;

    /**
     * The url field
     */
    @Getter
    @Setter
    private String url;
}
