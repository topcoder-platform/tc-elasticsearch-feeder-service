/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.job;

import java.util.Date;
import java.util.List;

import com.appirio.service.challengefeeder.dao.ChallengeListingFeederDAO;
import com.appirio.service.challengefeeder.dto.ChallengeFeederParam;
import com.appirio.service.challengefeeder.manager.ChallengeListingFeederManager;
import com.appirio.service.challengefeeder.util.JestClientUtils;
import com.appirio.supply.DAOFactory;
import com.appirio.supply.SupplyException;
import com.appirio.tech.core.api.v3.TCID;

import de.spinscale.dropwizard.jobs.annotations.DelayStart;
import de.spinscale.dropwizard.jobs.annotations.Every;
import lombok.NoArgsConstructor;

/**
 * LoadChangedChallengeListingJob is used to load the changed challenges
 * listing.
 * 
 * It's added in Topcoder ElasticSearch Feeder Service - Way To Populate Challenge-Listing Index v1.0
 * 
 * Version 2.0 - Topcoder Elasticsearch Feeder Service - Jobs Cleanup And Improvement v1.0
 * - refactor it to use the new configuration  
 * 
 * 
 * @author TCCoder
 * @version 2.0 
 *
 */
@DelayStart("14s")
@Every("${com.appirio.service.challengefeeder.job.LoadChangedChallengesListingJob}")
@NoArgsConstructor
public class LoadChangedChallengesListingJob extends BaseJob {
    /**
     * The challengeListingFeederManager field
     */
    private ChallengeListingFeederManager challengeListingFeederManager;

    /**
     * Init job
     *
     * @throws SupplyException if any error occurs
     */
    @Override
    protected void init() throws SupplyException {
        super.init();
        if (this.challengeListingFeederManager == null) {
            this.challengeListingFeederManager = new ChallengeListingFeederManager(JestClientUtils.get(this.config.getJestClientConfiguration()),
                    DAOFactory.getInstance().createDAO(ChallengeListingFeederDAO.class), this.config.getChallengeConfiguration());
        }
        if (this.indexName == null) {
            this.indexName = this.config.getJobsConfiguration().getLoadChangedChallengesListingJob().getIndexName();
            this.typeName = "challenges";
            this.lastRuntimestampeKey = this.indexName + BaseJob.JOB_LAST_RUN_TIMESTAMP_SUFFIX;
            this.lockerKey = this.getClass().getName() + "." + this.indexName + BaseJob.JOB_LOCKER_NAME_SUFFIX;
            this.jobEnableKey = this.indexName + BaseJob.JOB_ENABLE_SUFFIX;
            this.batchSize = this.config.getJobsConfiguration().getLoadChangedChallengesListingJob().getBatchUpdateSize();
        }
    }

    /**
     * Push feeders
     *
     * @param ids the ids to use
     * @throws SupplyException if any error occurs
     */
    @Override
    protected void pushFeeders(List<Long> ids) throws SupplyException {
        ChallengeFeederParam param = new ChallengeFeederParam();
        param.setIndex(this.indexName);
        param.setType(this.typeName);
        param.setChallengeIds(ids);

        this.challengeListingFeederManager.pushChallengeFeeder(param);
    }

    /**
     * Get feeder ids to push
     *
     * @param lastRunTimestamp the lastRunTimestamp to use
     * @throws SupplyException if any error occurs
     * @return the List<Long> result
     */
    @Override
    protected List<Long> getFeederIdsToPush(Date lastRunTimestamp) throws SupplyException {
        List<TCID> ids = this.challengeListingFeederManager.getChangedChallengeIds(new java.sql.Date(lastRunTimestamp.getTime()));
        return convertTCID(ids);
    }
}
