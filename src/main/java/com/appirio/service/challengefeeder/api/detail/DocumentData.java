/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api.detail;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the DocumentData model
 *
 * @author TCCoder
 * @version 1.0
 *
 */
public class DocumentData {
    /**
     * The challengeId field
     */
    @Getter
    @Setter
    @JsonIgnore
    private transient Long challengeId;

    /**
     * The documentName field
     */
    @Getter
    @Setter
    private String documentName;

    /**
     * The url field
     */
    @Getter
    @Setter
    private String url;
}
