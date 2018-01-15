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

import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.annotations.DelayStart;
import de.spinscale.dropwizard.jobs.annotations.OnApplicationStart;

/**
 * StartupJobForLoadChallengeChallenges is used to remove the redis cache values for LoadChallengeChallengesJob
 * 
 * It's added in Topcoder - Create CronJob For Populating Changed Challenges To Elasticsearch v1.0
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
@DelayStart("10s")
@OnApplicationStart
public class StartupJobForLoadChallengeChallenges extends Job {
    /**
     * Logger used to log events
     */
    private static final Logger logger = LoggerFactory.getLogger(StartupJobForLoadChallengeChallenges.class);
    
    /**
     * Do job
     *
     * @param context the context to use
     * @throws JobExecutionException if any error occurs
     */
    @Override
    public void doJob(JobExecutionContext context) throws JobExecutionException {
        if (LoadChangedChallengesJob.GLOBAL_CONFIGURATION.getRedissonConfiguration().isForceInitialLoad()) {
            ChallengeFeederServiceConfiguration config = LoadChangedChallengesJob.GLOBAL_CONFIGURATION;
            Config redissonConfig = new Config();
            redissonConfig.setLockWatchdogTimeout(config.getRedissonConfiguration().getLockWatchdogTimeout());
            redissonConfig.setUseLinuxNativeEpoll(config.getRedissonConfiguration().isUseLinuxNativeEpoll());
            if (config.getRedissonConfiguration().isClusterEnabled()) {
                for (String addr : config.getRedissonConfiguration().getNodeAdresses()) {
                    redissonConfig.useClusterServers().addNodeAddress(addr);
                }
               
            } else {
                redissonConfig.useSingleServer().setAddress(config.getRedissonConfiguration().getSingleServerAddress());
            }
            

            RedissonClient redisson = Redisson.create(redissonConfig);
            RMapCache<String, String> mapCache = redisson.getMapCache(config.getRedissonConfiguration().getLastRunTimestampPrefix());
            String time = mapCache.remove(config.getRedissonConfiguration().getLastRunTimestampPrefix());
            logger.info("Remove the last run time for challenge load job:" + time);
            
            redisson.shutdown();
        }
        
        
    }
}