/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the DatabaseTimestamp model 
 * 
 * It's added in Topcoder - Create CronJob For Populating Changed Challenges To Elasticsearch v1.0
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
public class DatabaseTimestamp {
    /**
     * The date field
     */
    @Getter
    @Setter
    private Date date;

}
