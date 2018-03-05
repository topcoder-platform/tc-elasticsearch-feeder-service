/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the FeederParam model 
 * 
 * It's added in Topcoder - Populate Marathon Match Related Data Into Challenge Model In Elasticsearch v1.0
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
public class FeederParam {
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
}
