/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the ChallengeFeederParam model
 * 
 * @author TCCoder
 * @version 1.0
 */
public class ChallengeFeederParam {
    /**
     * Represents the index attribute.
     */
    @Getter
    @Setter
    private String index;

    /**
     * Represents the type attribute.
     */
    @Getter
    @Setter
    private String type;

    /**
     * Represents the challenge ids attribute.
     */
    @Getter
    @Setter
    private List<Long> challengeIds;

}

