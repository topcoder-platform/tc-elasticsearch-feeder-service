/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.redisson.Redisson;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appirio.service.challengefeeder.ChallengeFeederServiceConfiguration;

import de.spinscale.dropwizard.jobs.annotations.DelayStart;
import de.spinscale.dropwizard.jobs.annotations.OnApplicationStart;

/**
 * StartupJob is used to remove the redis cache values for jobs
 * LoadChallengeChallengesJob, MarathonMatchesJob and SingleRoundMatchesJob.
 * 
 * It's added in Topcoder - Create CronJob For Populating Changed Challenges To
 * Elasticsearch v1.0
 * 
 * <p>
 * Changes in v1.1 (Topcoder - Create CronJob For Populating Marathon Matches
 * and SRMs To Elasticsearch v1.0):
 * <ul>
 * <li>Added reference to job MarathonMatchesJob.</li>
 * <li>Added reference to job SingleRoundMatchesJob.</li>
 * <li>Renamed class from StartupJobForLoadChallengeChallenges to StartupJob</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Version 1.2 - Topcoder ElasticSearch Feeder Service - Way To Populate Challenge-Detail Index For Legacy Marathon Matches v1.0
 * <ul>
 * <li>remove the last run time for the legacy mm.</li>
 * <li>remove the last run time for the legacy mm into challenge details.</li>
 * </ul>
 * </p>
 * 
 * @author TCCoder
 * @version 1.2 
 *
 */
@DelayStart("10s")
@OnApplicationStart
public class StartupJob extends BaseJob {

    /**
     * Logger used to log events
     */
    private static final Logger logger = LoggerFactory.getLogger(StartupJob.class);
    
    /**
     * Do job
     *
     * @param context the context to use
     * @throws JobExecutionException if any error occurs
     */
    @Override
    public void doJob(JobExecutionContext context) throws JobExecutionException {
        if (GLOBAL_CONFIGURATION.getRedissonConfiguration().isForceInitialLoad()) {
            ChallengeFeederServiceConfiguration config = GLOBAL_CONFIGURATION;
            Config redissonConfig = new Config();
            redissonConfig.setLockWatchdogTimeout(config.getRedissonConfiguration().getLockWatchdogTimeout());
            if (config.getRedissonConfiguration().isClusterEnabled()) {
                for (String addr : config.getRedissonConfiguration().getNodeAddresses()) {
                    redissonConfig.useClusterServers().addNodeAddress(addr);
                }
               
            } else {
                redissonConfig.useSingleServer().setAddress(config.getRedissonConfiguration().getSingleServerAddress());
            }

            RedissonClient redisson = Redisson.create(redissonConfig);
            RMapCache<String, String> mapCache = redisson.getMapCache(config.getRedissonConfiguration().getLoadChangedChallengesJobLastRunTimestampPrefix());
            String time = mapCache.remove(config.getRedissonConfiguration().getLoadChangedChallengesJobLastRunTimestampPrefix());
            logger.info("Remove the last run time for challenge load job:" + time);

            mapCache = redisson.getMapCache(config.getRedissonConfiguration().getMarathonMatchesJobLastRunTimestampPrefix());
            time = mapCache.remove(config.getRedissonConfiguration().getMarathonMatchesJobLastRunTimestampPrefix());
            logger.info("Remove the last run time for mm job:" + time);

            mapCache = redisson.getMapCache(config.getRedissonConfiguration().getSingleRoundMatchesJobLastRunTimestampPrefix());
            time = mapCache.remove(config.getRedissonConfiguration().getSingleRoundMatchesJobLastRunTimestampPrefix());
            logger.info("Remove the last run time for srm job:" + time);
            
            mapCache = redisson.getMapCache(config.getRedissonConfiguration().getLegacyMMJobLastRunTimestampPrefix());
            time = mapCache.remove(config.getRedissonConfiguration().getLegacyMMJobLastRunTimestampPrefix());
            logger.info("Remove the last run time for legacy mm into challenges job:" + time);
            
            mapCache = redisson.getMapCache(config.getRedissonConfiguration().getLoadChangedMMChallengeDetailJobLastRunTimestampPrefix());
            time = mapCache.remove(config.getRedissonConfiguration().getLoadChangedMMChallengeDetailJobLastRunTimestampPrefix());
            logger.info("Remove the last run time for legacy mm to challenge details job:" + time);

            redisson.shutdown();
        }
        
        
    }
}