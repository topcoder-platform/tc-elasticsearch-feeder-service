/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.job;

import com.appirio.service.challengefeeder.ChallengeFeederServiceConfiguration;
import com.appirio.service.challengefeeder.dao.ChallengeDetailFeederDAO;
import com.appirio.service.challengefeeder.dto.ChallengeFeederParam;
import com.appirio.service.challengefeeder.manager.ChallengeDetailFeederManager;
import com.appirio.service.challengefeeder.util.JestClientUtils;
import com.appirio.supply.DAOFactory;
import com.appirio.tech.core.api.v3.TCID;
import de.spinscale.dropwizard.jobs.annotations.DelayStart;
import de.spinscale.dropwizard.jobs.annotations.Every;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@DelayStart("15s")
@Every("${com.appirio.service.challengefeeder.job.LoadChangedChallengesDetailJob}")
public class LoadChangedChallengesDetailJob extends BaseJob {
    /**
     * Logger used to log events
     */
    private static final Logger logger = LoggerFactory.getLogger(LoadChangedChallengesDetailJob.class);

    /**
     * The challengeDetailFeederManager field
     */
    private ChallengeDetailFeederManager challengeDetailFeederManager;



    /**
     * Create LoadChangedChallengesDetailJob
     *
     * @param challengeDetailFeederManager the challengeDetailFeederManager to use
     * @param config the config to use
     */
    public LoadChangedChallengesDetailJob(ChallengeDetailFeederManager challengeDetailFeederManager, ChallengeFeederServiceConfiguration config) {
        this.challengeDetailFeederManager = challengeDetailFeederManager;
        this.config = config;
    }

    /**
     * Create LoadChangedChallengesDetailJob
     *
     */
    public LoadChangedChallengesDetailJob() {
    }

    /**
     * Do job. This methods load the challenges detail to elastic services.
     *
     * @param context the context to use
     * @throws JobExecutionException if any error occurs
     */
    @Override
    public void doJob(JobExecutionContext context) throws JobExecutionException {
        RLock lock;
        RedissonClient redisson = null;
        try {
            if (this.challengeDetailFeederManager == null) {
                this.challengeDetailFeederManager = new ChallengeDetailFeederManager(JestClientUtils.get(
                        GLOBAL_CONFIGURATION.getJestClientConfiguration()),
                        DAOFactory.getInstance().createDAO(ChallengeDetailFeederDAO.class));
            }
            if (this.config == null) {
                this.config = GLOBAL_CONFIGURATION;
            }
            this.challengeDetailFeederManager.setSubmissionImageUrl(this.config.getCommonConfiguration().getSubmissionImageUrl());
            Config redissonConfig = new Config();
            redissonConfig.setLockWatchdogTimeout(this.config.getRedissonConfiguration().getLockWatchdogTimeout());
            if (this.config.getRedissonConfiguration().isClusterEnabled()) {
                for (String addr : this.config.getRedissonConfiguration().getNodeAddresses()) {
                    redissonConfig.useClusterServers().addNodeAddress(addr);
                }

            } else {
                redissonConfig.useSingleServer().setAddress(this.config.getRedissonConfiguration().getSingleServerAddress());
            }

            logger.info("Try to get the lock");
            redisson = Redisson.create(redissonConfig);
            lock = redisson.getLock(config.getRedissonConfiguration().getLoadChangedChallengesDetailJobLockerKeyName());
            if (lock.tryLock()) {
                logger.info("Get the lock for challenges job successfully");
                try {
                    RMapCache<String, String> mapCache = redisson.getMapCache(config.getRedissonConfiguration().getLoadChangedChallengesDetailJobLastRunTimestampPrefix());

                    String timestamp = mapCache.get(config.getRedissonConfiguration().getLoadChangedChallengesDetailJobLastRunTimestampPrefix());

                    Date lastRunTimestamp = new Date(1L);
                    if (timestamp != null) {
                        lastRunTimestamp = DATE_FORMAT.parse(timestamp);
                    }

                    logger.info("The last run timestamp for challenges job is:" + timestamp);

                    String currentTime = DATE_FORMAT.format(this.challengeDetailFeederManager.getTimestamp());

                    List<TCID> totalIds = this.challengeDetailFeederManager.getChangedChallengeIds(new java.sql.Date(lastRunTimestamp.getTime()));

                    List<Long> ids = new ArrayList<>();
                    for (int i = 0; i < totalIds.size(); ++i) {
                        ids.add(Long.parseLong(totalIds.get(i).getId()));
                    }
                    logger.info("The count of the challenge ids to load:" + ids.size());
                    logger.info("The challenge ids to load:" + ids);

                    int batchSize = this.config.getRedissonConfiguration().getBatchUpdateSize();
                    int to = 0;
                    int from = 0;
                    while (to < ids.size()) {
                        to += (to + batchSize) > ids.size() ? (ids.size() - to) : batchSize;
                        List<Long> sub = ids.subList(from, to);
                        ChallengeFeederParam param = new ChallengeFeederParam();
                        param.setIndex(this.config.getRedissonConfiguration().getChallengesIndex());
                        param.setType(this.config.getRedissonConfiguration().getChallengesDetailType());
                        param.setChallengeIds(sub);
                        try {
                            this.challengeDetailFeederManager.pushChallengeFeeder(param);
                        } catch (Exception e) {
                            // ignore all exception
                            e.printStackTrace();
                        }

                        from = to;
                    }

                    logger.info("update last run timestamp for challenges job is:" + currentTime);
                    mapCache.put(config.getRedissonConfiguration().getLoadChangedChallengesDetailJobLastRunTimestampPrefix(), currentTime);
                } finally {
                    logger.info("release the lock for challenges job");
                    lock.unlock();
                }
            } else {
                logger.warn("the previous challenges job is still running");
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        } finally {
            if (redisson != null) {
                redisson.shutdown();
            }
        }
    }
}
