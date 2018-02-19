/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the DataScienceFeederParam model
 * <p>
 * Added in Topcoder - Add Endpoints To Populating Marathon Matches And SRMs Into Elasticsearch v1.0
 * </p>
 *
 * @author TCCoder
 * @version 1.0
 */
public class DataScienceFeederParam {
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
     * Represents the round ids attribute.
     */
    @Getter
    @Setter
    private List<Long> roundIds;

}

