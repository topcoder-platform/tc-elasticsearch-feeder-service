/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * The common configuration
 *
 * @author TCSCODER
 * @version 1.0
 */
public class CommonConfiguration {
    /**
     * The studio submission url template
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String submissionImageUrl;
}
