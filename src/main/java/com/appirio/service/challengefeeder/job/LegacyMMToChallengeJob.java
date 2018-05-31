/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.job;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.appirio.service.challengefeeder.dao.*;
import com.appirio.service.challengefeeder.dto.*;
import com.appirio.service.challengefeeder.manager.*;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appirio.service.challengefeeder.ChallengeFeederServiceConfiguration;
import com.appirio.service.challengefeeder.util.JestClientUtils;
import com.appirio.supply.DAOFactory;
import com.appirio.tech.core.api.v3.TCID;

import de.spinscale.dropwizard.jobs.annotations.DelayStart;
import de.spinscale.dropwizard.jobs.annotations.Every;
import lombok.NoArgsConstructor;

/**
 * LegacyMMToChallengeJob is used to populate legacy marathon matches to elasticsearch challenge index.
 *
 * @author TCCoder
 * @version 1.0
 *
 */
@DelayStart("20s")
@Every("${com.appirio.service.challengefeeder.job.LegacyMMToChallengeJob}")
@NoArgsConstructor
public class LegacyMMToChallengeJob extends BaseJob {

    /**
     * The marathon match - challenge feeder manager instance.
     */
    private MmFeederManager manager;

    /**
     * Logger used to log events
     */
    private static final Logger logger = LoggerFactory.getLogger(LegacyMMToChallengeJob.class);

    /**
     * LegacyMMToChallengeJob constructor.
     *
     * @param manager the MmFeederManager to use
     * @param config the config to use
     */
    public LegacyMMToChallengeJob(MmFeederManager manager, ChallengeFeederServiceConfiguration config) {
        super(config);
        this.manager = manager;
    }

    /**
     * Do job. Load the legacy marathon matches to elasticsearch service.
     *
     * @param context the job context to use
     * @throws JobExecutionException if any error occurs
     */
    @Override
    public void doJob(JobExecutionContext context) throws JobExecutionException {
        RLock lock = null;
        RedissonClient redisson = null;
        try {
            if (this.manager == null) {
                this.manager = new MmFeederManager(JestClientUtils.get(GLOBAL_CONFIGURATION.getJestClientConfiguration()), DAOFactory.getInstance().createDAO(MmFeederDAO.class));
            }
            if (this.config == null) {
                this.config = GLOBAL_CONFIGURATION;
            }
            Config redissonConfig = new Config();
            redissonConfig.setLockWatchdogTimeout(this.config.getRedissonConfiguration().getLockWatchdogTimeout());
            if (this.config.getRedissonConfiguration().isClusterEnabled()) {
                for (String addr : this.config.getRedissonConfiguration().getNodeAddresses()) {
                    redissonConfig.useClusterServers().addNodeAddress(addr);
                }
            } else {
                redissonConfig.useSingleServer().setAddress(this.config.getRedissonConfiguration().getSingleServerAddress());
            }

            logger.info("Try to get the lock for legacy marathon matches job");
            redisson = Redisson.create(redissonConfig);
            lock = redisson.getLock(config.getRedissonConfiguration().getLegacyMMJobLockerKeyName());
            if (lock.tryLock()) {
                logger.info("Get the lock for legacy marathon matches job successfully");
                try {
                    RMapCache<String, String> mapCache = redisson.getMapCache(config.getRedissonConfiguration().getLegacyMMJobLastRunTimestampPrefix());
                    String timestamp = mapCache.get(config.getRedissonConfiguration().getLegacyMMJobLastRunTimestampPrefix());

                    Date lastRunTimestamp = new Date(INITIAL_TIMESTAMP);
                    if (timestamp != null) {
                        lastRunTimestamp = DATE_FORMAT.parse(timestamp);
                    }

                    logger.info("The last run timestamp for marathon matches job is:" + lastRunTimestamp);

                    Date currentTimestamp = new Date();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(currentTimestamp);
                    calendar.add(Calendar.DAY_OF_MONTH, this.config.getRedissonConfiguration().getMarathonMatchesDaysToSubtract());
                    Date dateParam = calendar.getTime();

                    logger.info("The initial timestamp for legacy marathon matches job is:" + dateParam);

                    List<TCID> totalIds = this.manager.getMatchesWithRegistrationPhaseStartedIds(new java.sql.Date(dateParam.getTime()), lastRunTimestamp.getTime());

                    List<Long> ids = new ArrayList<>();
                    for (int i = 0; i < totalIds.size(); ++i) {
                        ids.add(Long.parseLong(totalIds.get(i).getId()));
                    }

                    logger.info("The count of the MM ids to load:" + ids.size());
                    logger.info("The MM ids to load:" + ids);

                    int batchSize = this.config.getRedissonConfiguration().getBatchUpdateSize();
                    int to = 0;
                    int from = 0;
                    while (to < ids.size()) {
                        to += (to + batchSize) > ids.size() ? (ids.size() - to) : batchSize;
                        List<Long> sub = ids.subList(from, to);
                        MmFeederParam param = new MmFeederParam();
                        param.setIndex(this.config.getRedissonConfiguration().getChallengesIndex());
                        param.setType(this.config.getRedissonConfiguration().getChallengesType());
                        param.setRoundIds(sub);
                        param.setLegacy(Boolean.TRUE);
                        try {
                            this.manager.pushMarathonMatchDataIntoChallenge(param);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                        from = to;
                    }

                    // mark last execution as current timestamp
                    logger.info("update last run timestamp for challenges job is:" + currentTimestamp);
                    mapCache.put(config.getRedissonConfiguration().getLegacyMMJobLastRunTimestampPrefix(), DATE_FORMAT.format(currentTimestamp));
                } finally {
                    logger.info("release the lock for legacy marathon matches job");
                    lock.unlock();
                }
            } else {
                logger.warn("the previous job for legacy marathon matches job is still running");
            }
        } catch(Exception exp) {
            exp.printStackTrace();
        } finally {
            if (redisson != null) {
                redisson.shutdown();
            }
        }
    }

}
