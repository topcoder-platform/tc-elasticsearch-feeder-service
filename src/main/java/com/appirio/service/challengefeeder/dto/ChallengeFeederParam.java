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
 * Version 1.1 - Topcoder - Populate Marathon Match Related Data Into Challenge Model In Elasticsearch v1.0
 * - It's changed to extend from FeederParam
 * 
 * @author TCCoder
 * @version 1.1 
 */
public class ChallengeFeederParam extends FeederParam{

    /**
     * Represents the challenge ids attribute.
     */
    @Getter
    @Setter
    private List<Long> challengeIds;
}

