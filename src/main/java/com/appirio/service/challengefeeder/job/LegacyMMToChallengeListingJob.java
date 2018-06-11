/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.job;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.appirio.service.challengefeeder.dao.ChallengeListingMMFeederDAO;
import com.appirio.service.challengefeeder.dto.MmFeederParam;
import com.appirio.service.challengefeeder.manager.ChallengeListingMMFeederManager;
import com.appirio.service.challengefeeder.util.JestClientUtils;
import com.appirio.supply.DAOFactory;
import com.appirio.supply.SupplyException;
import com.appirio.tech.core.api.v3.TCID;

import de.spinscale.dropwizard.jobs.annotations.DelayStart;
import de.spinscale.dropwizard.jobs.annotations.Every;
import lombok.NoArgsConstructor;

/**
 * LegacyMMToChallengeListingJob is used to populate legacy marathon matches to elasticsearch challenge listing index.
 * 
 * Version 2.0 - Topcoder Elasticsearch Feeder Service - Jobs Cleanup And Improvement v1.0
 * - refactor it to use the new configuration 
 * 
 *
 * @author TCCoder
 * @version 2.0
 *
 */
@DelayStart("19s")
@Every("${com.appirio.service.challengefeeder.job.LegacyMMToChallengeListingJob}")
@NoArgsConstructor
public class LegacyMMToChallengeListingJob extends BaseJob {

    /**
     * The marathon match - challenge feeder manager instance.
     */
    private ChallengeListingMMFeederManager manager;

    /**
     * Init the job
     *
     * @throws SupplyException if any error occurs
     */
    @Override
    protected void init() throws SupplyException {
        super.init();
        if (this.manager == null) {
            this.manager = new ChallengeListingMMFeederManager(JestClientUtils.get(this.config.getJestClientConfiguration()), 
                    DAOFactory.getInstance().createDAO(ChallengeListingMMFeederDAO.class), 
                    this.config.getJobsConfiguration().getLegacyMMToChallengeListingJob().getMarathonMatchesForumUrl());
        }
        if (this.indexName == null) {
            this.indexName = this.config.getJobsConfiguration().getLegacyMMToChallengeListingJob().getIndexName();
            this.typeName = "challenges";
            this.lastRuntimestampeKey = this.indexName + BaseJob.JOB_LAST_RUN_TIMESTAMP_SUFFIX;
            this.lockerKey = this.indexName + BaseJob.JOB_LOCKER_NAME_SUFFIX;
            this.jobEnableKey = this.indexName + BaseJob.JOB_ENABLE_SUFFIX;
            this.batchSize = this.config.getJobsConfiguration().getLegacyMMToChallengeListingJob().getBatchUpdateSize();
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
        MmFeederParam param = new MmFeederParam();
        param.setIndex(this.indexName);
        param.setType(this.typeName);
        param.setRoundIds(ids);
        param.setLegacy(Boolean.TRUE);
        try {
            this.manager.pushMarathonMatchDataIntoChallenge(param);
        } catch(Exception e) {
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
        Date currentTimestamp = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTimestamp);
        calendar.add(Calendar.DAY_OF_MONTH, this.config.getJobsConfiguration().getLegacyMMToChallengeListingJob().getMarathonMatchesDaysToSubtract());
        Date dateParam = calendar.getTime();

        List<TCID> totalIds = this.manager.getMatchesWithRegistrationPhaseStartedIds(new java.sql.Date(dateParam.getTime()), lastRunTimestamp.getTime());
        
        return convertTCID(totalIds);
    }

}
