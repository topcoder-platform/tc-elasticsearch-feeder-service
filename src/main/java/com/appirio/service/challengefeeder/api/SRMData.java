/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the SRMData model.
 * <p>
 * Added in Topcoder - Add Endpoints To Populating Marathon Matches And SRMs Into Elasticsearch v1.0
 * </p>
 *
 * @author TCCoder
 * @version 1.0
 */
public class SRMData extends DataScienceData {
    /**
     * The isRatedForMM field
     */
    @Getter
    @Setter
    private List<String> isRatedForSRM;
}
