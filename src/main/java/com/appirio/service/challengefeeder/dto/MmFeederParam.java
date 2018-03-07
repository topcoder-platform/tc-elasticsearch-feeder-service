/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the MmFeederParam model 
 * 
 * It's added in Topcoder - Populate Marathon Match Related Data Into Challenge Model In Elasticsearch v1.0
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
public class MmFeederParam extends FeederParam {
    /**
     * The round ids attribute.
     */
    @Getter
    @Setter
    private List<Long> roundIds;
}
