/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.appirio.service.challengefeeder.dao.ChallengeFeederDAO;
import com.appirio.service.challengefeeder.dao.ChallengeListingFeederDAO;
import com.appirio.service.challengefeeder.dto.ChallengeFeederParam;
import com.appirio.service.challengefeeder.manager.ChallengeListingFeederManager;
import com.appirio.service.challengefeeder.util.JestClientUtils;
import com.appirio.supply.DAOFactory;
import com.appirio.tech.core.api.v3.TCID;

import de.spinscale.dropwizard.jobs.annotations.DelayStart;
import de.spinscale.dropwizard.jobs.annotations.Every;

/**
 * LoadChangedChallengeListingJob is used to load the changed challenges listing.
 * 
 * It's added in Topcoder ElasticSearch Feeder Service - Way To Populate Challenge-Listing Index v1.0
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
@DelayStart("14s")
@Every("${com.appirio.service.challengefeeder.job.LoadChangedChallengesListingJob}")
public class LoadChangedChallengesListingJob extends BaseJob {
    /**
     * Logger used to log events
     */
    private static final Logger logger = LoggerFactory.getLogger(LoadChangedChallengesListingJob.class);

    /**
     * The challengeListingFeederManager field
     */
    private ChallengeListingFeederManager challengeListingFeederManager;
    
    /**
     * Create LoadChangedChallengesListingJob
     *
     * @param challengeListingFeederManager the challengeListingFeederManager to use
     * @param config the config to use
     */
    public LoadChangedChallengesListingJob(ChallengeListingFeederManager challengeListingFeederManager, ChallengeFeederServiceConfiguration config) {
        this.challengeListingFeederManager = challengeListingFeederManager;
        this.config = config;
    }
    
    /**
     * Create LoadChangedChallengesListingJob
     *
     */
    public LoadChangedChallengesListingJob() {
    }
    
    /**
     * Do job. This methods load the challenges to elastic services.
     *
     * @param context the context to use
     * @throws JobExecutionException if any error occurs
     */
    @Override
    public void doJob(JobExecutionContext context) throws JobExecutionException {
        RLock lock;
        RedissonClient redisson = null;
        try {
            if (this.challengeListingFeederManager == null) {
                this.challengeListingFeederManager = new ChallengeListingFeederManager(JestClientUtils.get(GLOBAL_CONFIGURATION.getJestClientConfiguration()), 
                        DAOFactory.getInstance().createDAO(ChallengeFeederDAO.class), 
                        DAOFactory.getInstance().createDAO(ChallengeListingFeederDAO.class),
                        GLOBAL_CONFIGURATION.getChallengeConfiguration());
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
            
            logger.info("Try to get the lock for the challenges listing job");
            redisson = Redisson.create(redissonConfig);
            lock = redisson.getLock(config.getRedissonConfiguration().getLoadChangedChallengesListingJobLockerKeyName());
            if (lock.tryLock()) {
                logger.info("Get the lock for challenges listing job successfully");
                try {
                    RMapCache<String, String> mapCache = redisson.getMapCache(config.getRedissonConfiguration().getLoadChangedChallengesListingJobLastRunTimestampPrefix());

                    String timestamp = mapCache.get(config.getRedissonConfiguration().getLoadChangedChallengesListingJobLastRunTimestampPrefix());

                    Date lastRunTimestamp = new Date(1L);
                    if (timestamp != null) {
                        lastRunTimestamp = DATE_FORMAT.parse(timestamp);
                    }

                    logger.info("The last run timestamp for challenges listing job is:" + timestamp);

                    String currentTime = DATE_FORMAT.format(new Date());
                    List<TCID> totalIds = this.challengeListingFeederManager.getChangedChallengeIds(new java.sql.Date(lastRunTimestamp.getTime()));

                    List<Long> ids = new ArrayList<>();
                    for (int i = 0; i < totalIds.size(); ++i) {
                        ids.add(Long.parseLong(totalIds.get(i).getId()));
                    }
                    logger.info("The count of the challenge ids for listing to load:" + ids.size());
                    logger.info("The challenge ids for listing to load:" + ids);

                    int batchSize = this.config.getRedissonConfiguration().getBatchUpdateSize();
                    int to = 0;
                    int from = 0;
                    while (to < ids.size()) {
                        to += (to + batchSize) > ids.size() ? (ids.size() - to) : batchSize;
                        List<Long> sub = ids.subList(from, to);
                        ChallengeFeederParam param = new ChallengeFeederParam();
                        param.setIndex(this.config.getRedissonConfiguration().getChallengesListingIndex());
                        param.setType(this.config.getRedissonConfiguration().getChallengesListingType());
                        param.setChallengeIds(sub);
                        try {
                            this.challengeListingFeederManager.pushChallengeFeeder(param);
                        } catch (Exception e) {
                            // ignore all exception
                            e.printStackTrace();
                        }

                        from = to;
                    }

                    logger.info("update last run timestamp for challenges listing job is:" + currentTime);
                    mapCache.put(config.getRedissonConfiguration().getLoadChangedChallengesListingJobLastRunTimestampPrefix(), currentTime);
                } finally {
                    logger.info("release the lock for challenges listing job");
                    lock.unlock();
                }
            } else {
                logger.warn("the previous challenges listing job is still running");
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
