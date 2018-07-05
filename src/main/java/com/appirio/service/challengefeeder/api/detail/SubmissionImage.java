/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api.detail;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the SubmissionImage model
 *
 * @author TCCoder
 * @version 1.0
 *
 */
public class SubmissionImage {
    /**
     * The tiny field
     */
    @Getter
    @Setter
    private String tiny;

    /**
     * The small field
     */
    @Getter
    @Setter
    private String small;

    /**
     * The medium field
     */
    @Getter
    @Setter
    private String medium;

    /**
     * The full field
     */
    @Getter
    @Setter
    private String full;

    /**
     * The thumb field
     */
    @Getter
    @Setter
    private String thumb;

    /**
     * The previewPackage field
     */
    @Getter
    @Setter
    private String previewPackage;
}
