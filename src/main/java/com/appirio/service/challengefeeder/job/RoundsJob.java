/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.job;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.appirio.service.challengefeeder.dao.RoundFeederDAO;
import com.appirio.service.challengefeeder.dto.DataScienceFeederParam;
import com.appirio.service.challengefeeder.manager.RoundFeederManager;
import com.appirio.service.challengefeeder.util.JestClientUtils;
import com.appirio.supply.DAOFactory;
import com.appirio.supply.SupplyException;
import com.appirio.tech.core.api.v3.TCID;

import de.spinscale.dropwizard.jobs.annotations.DelayStart;
import de.spinscale.dropwizard.jobs.annotations.Every;
import lombok.NoArgsConstructor;

/**
 * RoundsJob is used to populate the rounds data into the elasticsearch, including the single round matches and marathon matches.
 * 
 * It's added in TC Elasticsearch feeder - Add Job For populating rounds index v1.0
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
@DelayStart("30s")
@Every("${com.appirio.service.challengefeeder.job.RoundsJob}")
@NoArgsConstructor
public class RoundsJob extends BaseJob {
    
    /**
     * The round feeder manager instance.
     */
    private RoundFeederManager manager;

    /**
     * Init the job
     *
     * @throws SupplyException if any error occurs
     */
    @Override
    protected void init() throws SupplyException {
        super.init();
        if (this.manager == null) {
            this.manager = new RoundFeederManager(JestClientUtils.get(this.config.getJestClientConfiguration()), 
                    DAOFactory.getInstance().createDAO(RoundFeederDAO.class));
        }
        if (this.indexName == null) {
            this.indexName = this.config.getJobsConfiguration().getRoundsJob().getIndexName();
            this.typeName = "round";
            this.lastRuntimestampeKey = this.indexName + BaseJob.JOB_LAST_RUN_TIMESTAMP_SUFFIX;
            this.lockerKey = this.getClass().getName() + "." + this.indexName + BaseJob.JOB_LOCKER_NAME_SUFFIX;
            this.jobEnableKey = this.indexName + BaseJob.JOB_ENABLE_SUFFIX;
            this.batchSize = this.config.getJobsConfiguration().getRoundsJob().getBatchUpdateSize();
        }
    }

    /**
     * Push feeders
     *
     * @param ids the round ids to use
     * @throws SupplyException if any error occurs
     */
    @Override
    protected void pushFeeders(List<Long> ids) throws SupplyException {
        DataScienceFeederParam param = new DataScienceFeederParam();

        param.setIndex(this.indexName);
        param.setType(this.typeName);
        param.setRoundIds(ids);
        this.manager.pushRoundFeeder(param);
    }

    /**
     * Get round feeder ids to push
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
        calendar.add(Calendar.DAY_OF_MONTH, this.config.getJobsConfiguration().getRoundsJob().getRoundsDaysToSubtract());
        Date dateParam = calendar.getTime();
        List<TCID> totalIds = this.manager.getMatchesWithRegistrationPhaseStartedIds(new java.sql.Date(dateParam.getTime()), lastRunTimestamp.getTime());
        
        return convertTCID(totalIds);
    }
}
