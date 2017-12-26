/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the CheckpointPrizeData model 
 * 
 * @author TCCoder
 * @version 1.0
 */
public class CheckpointPrizeData extends PrizeData {
    /**
     * Represents the number of submission attribute.
     */
    @Getter
    @Setter
    private Integer numberOfSubmission;
}

