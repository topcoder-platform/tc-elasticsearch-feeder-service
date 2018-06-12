/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.job;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.appirio.service.challengefeeder.dao.MarathonMatchFeederDAO;
import com.appirio.service.challengefeeder.dto.DataScienceFeederParam;
import com.appirio.service.challengefeeder.manager.MarathonMatchFeederManager;
import com.appirio.service.challengefeeder.util.JestClientUtils;
import com.appirio.supply.DAOFactory;
import com.appirio.supply.SupplyException;
import com.appirio.tech.core.api.v3.TCID;

import de.spinscale.dropwizard.jobs.annotations.DelayStart;
import de.spinscale.dropwizard.jobs.annotations.Every;
import lombok.NoArgsConstructor;

/**
 * MarathonMatchesJob is used to populate marathon matches to elasticsearch.
 * 
 * It was added in Topcoder - Create CronJob For Populating Marathon Matches and SRMs To Elasticsearch v1.0
 * 
 * Version 2.0 - Topcoder Elasticsearch Feeder Service - Jobs Cleanup And Improvement v1.0
 * - refactor it to use the new configuration 
 * 
 * 
 * @author TCCoder
 * @version 2.0 
 *
 */
@DelayStart("20s")
@Every("${com.appirio.service.challengefeeder.job.MarathonMatchesJob}")
@NoArgsConstructor
public class MarathonMatchesJob extends BaseJob {
    /**
     * The marathon match feeder manager instance.
     */
    private MarathonMatchFeederManager manager;

    /**
     * Init the job
     *
     * @throws SupplyException if any error occurs
     */
    @Override
    protected void init() throws SupplyException {
        super.init();
        if (this.manager == null) {
            this.manager = new MarathonMatchFeederManager(JestClientUtils.get(this.config.getJestClientConfiguration()), 
                    DAOFactory.getInstance().createDAO(MarathonMatchFeederDAO.class));
        }
        if (this.indexName == null) {
            this.indexName = this.config.getJobsConfiguration().getMarathonMatchesJob().getIndexName();
            this.typeName = "mmatches";
            this.lastRuntimestampeKey = this.indexName + BaseJob.JOB_LAST_RUN_TIMESTAMP_SUFFIX;
            this.lockerKey = this.getClass().getName() + "." + this.indexName + BaseJob.JOB_LOCKER_NAME_SUFFIX;
            this.jobEnableKey = this.indexName + BaseJob.JOB_ENABLE_SUFFIX;
            this.batchSize = this.config.getJobsConfiguration().getMarathonMatchesJob().getBatchUpdateSize();
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
        DataScienceFeederParam param = new DataScienceFeederParam();
        param.setIndex(this.indexName);
        param.setType(this.typeName);
        param.setRoundIds(ids);
        try {
             this.manager.pushMarathonMatchFeeder(param);
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
        calendar.add(Calendar.DAY_OF_MONTH, this.config.getJobsConfiguration().getMarathonMatchesJob().getMarathonMatchesDaysToSubtract());
        Date dateParam = calendar.getTime();
        List<TCID> totalIds = this.manager.getMatchesWithRegistrationPhaseStartedIds(new java.sql.Date(dateParam.getTime()), lastRunTimestamp.getTime());
        
        return convertTCID(totalIds);
    }
}
