package com.appirio.service.challengefeeder.job;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.appirio.service.challengefeeder.dao.ChallengeDetailMMFeederDAO;
import com.appirio.service.challengefeeder.dto.MmFeederParam;
import com.appirio.service.challengefeeder.manager.ChallengeDetailMMFeederManager;
import com.appirio.service.challengefeeder.util.JestClientUtils;
import com.appirio.supply.DAOFactory;
import com.appirio.supply.SupplyException;
import com.appirio.tech.core.api.v3.TCID;

import de.spinscale.dropwizard.jobs.annotations.DelayStart;
import de.spinscale.dropwizard.jobs.annotations.Every;
import lombok.NoArgsConstructor;

/**
 * LoadChangedMMChallengeDetailJob is used to populate legacy marathon matches to elasticsearch challenge detail.
 * 
 * Version 2.0 - Topcoder Elasticsearch Feeder Service - Jobs Cleanup And Improvement v1.0
 * - refactor it to use the new configuration 
 * 
 *
 * @author TCCoder
 * @version 2.0 
 *
 */
@DelayStart("21s")
@Every("${com.appirio.service.challengefeeder.job.LoadChangedMMChallengeDetailJob}")
@NoArgsConstructor
public class LoadChangedMMChallengeDetailJob extends BaseJob {

    /**
     * The marathon match - challenge detail feeder manager instance.
     */
    private ChallengeDetailMMFeederManager manager;

    /**
     * Init the job
     *
     * @throws SupplyException if any error occurs
     */
    @Override
    protected void init() throws SupplyException {
        super.init();
        if (this.manager == null) {
            this.manager = new ChallengeDetailMMFeederManager(JestClientUtils.get(this.config.getJestClientConfiguration()), 
                    DAOFactory.getInstance().createDAO(ChallengeDetailMMFeederDAO.class));
        }
        
        if (this.indexName == null) {
            this.indexName = this.config.getJobsConfiguration().getLoadChangedMMChallengeDetailJob().getIndexName();
            this.typeName = "challenges";
            this.lastRuntimestampeKey = this.indexName + BaseJob.JOB_LAST_RUN_TIMESTAMP_SUFFIX;
            this.lockerKey = this.getClass().getName() + "." + this.indexName + BaseJob.JOB_LOCKER_NAME_SUFFIX;
            this.jobEnableKey = this.indexName + BaseJob.JOB_ENABLE_SUFFIX;
            this.batchSize = this.config.getJobsConfiguration().getLoadChangedMMChallengeDetailJob().getBatchUpdateSize();
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
            this.manager.pushMarathonMatchDataIntoChallengeDetail(param);
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
        calendar.add(Calendar.DAY_OF_MONTH, this.config.getJobsConfiguration().getLoadChangedMMChallengeDetailJob().getMarathonMatchesDaysToSubtract());
        Date dateParam = calendar.getTime();

        List<TCID> totalIds = this.manager.getMatchesWithRegistrationPhaseStartedIds(new java.sql.Date(dateParam.getTime()), lastRunTimestamp.getTime());
        
        return convertTCID(totalIds);
    }
}

