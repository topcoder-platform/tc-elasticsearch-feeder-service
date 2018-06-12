/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.job;

import com.appirio.service.challengefeeder.dao.ChallengeDetailFeederDAO;
import com.appirio.service.challengefeeder.dto.ChallengeFeederParam;
import com.appirio.service.challengefeeder.manager.ChallengeDetailFeederManager;
import com.appirio.service.challengefeeder.util.JestClientUtils;
import com.appirio.supply.DAOFactory;
import com.appirio.supply.SupplyException;
import com.appirio.tech.core.api.v3.TCID;
import de.spinscale.dropwizard.jobs.annotations.DelayStart;
import de.spinscale.dropwizard.jobs.annotations.Every;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * LoadChangedChallengesDetailJob is used to load the challenge details data into the details index
 * 
 * Version 2.0 - Topcoder Elasticsearch Feeder Service - Jobs Cleanup And Improvement v1.0
 * - refactor it to use the new configuration 
 * 
 * 
 * @author TCCoder
 * @version 2.0 
 *
 */
@DelayStart("16s")
@Every("${com.appirio.service.challengefeeder.job.LoadChangedChallengesDetailJob}")
@NoArgsConstructor
public class LoadChangedChallengesDetailJob extends BaseJob {
    /**
     * The challengeDetailFeederManager field
     */
    private ChallengeDetailFeederManager challengeDetailFeederManager;

    /**
     * Init job
     *
     * @throws SupplyException if any error occurs
     */
    @Override
    protected void init() throws SupplyException {
        super.init();
        if (this.challengeDetailFeederManager == null) {
            this.challengeDetailFeederManager = new ChallengeDetailFeederManager(JestClientUtils.get(
                    this.config.getJestClientConfiguration()),
                    DAOFactory.getInstance().createDAO(ChallengeDetailFeederDAO.class));
        }
        if (this.indexName == null) {
            this.indexName = this.config.getJobsConfiguration().getLoadChangedChallengesDetailJob().getIndexName();
            this.typeName = "challenges";
            this.lastRuntimestampeKey = this.indexName + BaseJob.JOB_LAST_RUN_TIMESTAMP_SUFFIX;
            this.lockerKey = this.getClass().getName() + "." + this.indexName + BaseJob.JOB_LOCKER_NAME_SUFFIX;
            this.jobEnableKey = this.indexName + BaseJob.JOB_ENABLE_SUFFIX;
            this.batchSize = this.config.getJobsConfiguration().getLoadChangedChallengesDetailJob().getBatchUpdateSize();
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
        try {
            this.challengeDetailFeederManager.pushChallengeFeeder(param);
        } catch (Exception e) {
            // ignore all exception
            e.printStackTrace();
        }
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
        List<TCID> totalIds = this.challengeDetailFeederManager.getChangedChallengeIds(new java.sql.Date(lastRunTimestamp.getTime()));
        return convertTCID(totalIds);
    }
}
