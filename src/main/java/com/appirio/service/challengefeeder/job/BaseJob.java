/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */

package com.appirio.service.challengefeeder.job;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.appirio.service.challengefeeder.ChallengeFeederServiceConfiguration;

import de.spinscale.dropwizard.jobs.Job;
import lombok.NoArgsConstructor;

/**
 * BaseJob abstract class used as base class to children job classes.
 * 
 * It was added in Topcoder - Create CronJob For Populating Marathon Matches and SRMs To Elasticsearch v1.0
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
@NoArgsConstructor
public abstract class BaseJob extends Job {

    /**
     * The GLOBAL_CONFIGURATION field.
     */
    public static ChallengeFeederServiceConfiguration GLOBAL_CONFIGURATION;
    
    /**
     * The initial timestamp constant.
     */
    protected static final long INITIAL_TIMESTAMP = 1L;

    /**
     * The DATE_FORMAT field.
     */
    protected static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    static {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /**
     * The config field.
     */
    protected ChallengeFeederServiceConfiguration config;

    /**
     * BaseJob constructor.
     * 
     * @param config the config to use.
     */
    public BaseJob(ChallengeFeederServiceConfiguration config) {
        this.config = config;
    }

}
