/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.job;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.appirio.supply.SupplyException;
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
import com.appirio.service.challengefeeder.dto.ChallengeFeederParam;
import com.appirio.service.challengefeeder.manager.ChallengeFeederManager;
import com.appirio.service.challengefeeder.util.JestClientUtils;
import com.appirio.supply.DAOFactory;
import com.appirio.tech.core.api.v3.TCID;

import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.annotations.DelayStart;
import de.spinscale.dropwizard.jobs.annotations.Every;

/**
 * LoadChangedChallengesJob is used to load the changed challenges.
 * 
 * It's added in Topcoder - Create CronJob For Populating Changed Challenges To Elasticsearch v1.0
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
@DelayStart("15s")
@Every("${com.appirio.service.challengefeeder.job.LoadChangedChallengesJob}")
public class LoadChangedChallengesJob extends Job {
    /**
     * The GLOBAL_CONFIGURATION field
     */
    public static ChallengeFeederServiceConfiguration GLOBAL_CONFIGURATION;
    
    /**
     * Logger used to log events
     */
    private static final Logger logger = LoggerFactory.getLogger(LoadChangedChallengesJob.class);
    
    /**
     * The DATE_FORMAT field
     */
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    static {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /**
     * The challengeFeederManager field
     */
    private ChallengeFeederManager challengeFeederManager;
    
    /**
     * The config field
     */
    private ChallengeFeederServiceConfiguration config;
    
    /**
     * Create LoadChangedChallengesJob
     *
     * @param challengeFeederManager the challengeFeederManager to use
     * @param config the config to use
     */
    public LoadChangedChallengesJob(ChallengeFeederManager challengeFeederManager, ChallengeFeederServiceConfiguration config) {
        this.challengeFeederManager = challengeFeederManager;
        this.config = config;
    }
    
    /**
     * Create LoadChangedChallengesJob
     *
     */
    public LoadChangedChallengesJob() {
    }
    
    /**
     * Do job. This methods load the challenges to elastic services.
     *
     * @param context the context to use
     * @throws JobExecutionException if any error occurs
     */
    @Override
    public void doJob(JobExecutionContext context) throws JobExecutionException {
        RLock lock = null;
        RedissonClient redisson = null;
        try {
            if (this.challengeFeederManager == null) {
                this.challengeFeederManager = new ChallengeFeederManager(JestClientUtils.get(GLOBAL_CONFIGURATION.getJestClientConfiguration()), DAOFactory.getInstance().createDAO(ChallengeFeederDAO.class));
            }
            if (this.config == null) {
                this.config = GLOBAL_CONFIGURATION;
            }
            Config redissonConfig = new Config();
            redissonConfig.setLockWatchdogTimeout(this.config.getRedissonConfiguration().getLockWatchdogTimeout());
            redissonConfig.setUseLinuxNativeEpoll(this.config.getRedissonConfiguration().isUseLinuxNativeEpoll());
            if (this.config.getRedissonConfiguration().isClusterEnabled()) {
                for (String addr : this.config.getRedissonConfiguration().getNodeAdresses()) {
                    redissonConfig.useClusterServers().addNodeAddress(addr);
                }
               
            } else {
                redissonConfig.useSingleServer().setAddress(this.config.getRedissonConfiguration().getSingleServerAddress());
            }
            
            logger.info("Try to get the lock");
            redisson = Redisson.create(redissonConfig);
            lock = redisson.getLock(config.getRedissonConfiguration().getLockerKeyName());

            if (lock.tryLock()) {
                logger.info("Get the lock successfully");
                try {
                    RMapCache<String, String> mapCache = redisson.getMapCache(config.getRedissonConfiguration().getLastRunTimestampPrefix());

                    String timestamp = mapCache.get(config.getRedissonConfiguration().getLastRunTimestampPrefix());

                    Date lastRunTimestamp = new Date(1L);
                    if (timestamp != null) {
                        lastRunTimestamp = DATE_FORMAT.parse(timestamp);
                    }

                    logger.info("The last run timestamp is:" + timestamp);

                    String currentTime = DATE_FORMAT.format(this.challengeFeederManager.getTimestamp());

                    logger.info("update last run timestamp is:" + timestamp);
                    mapCache.put(config.getRedissonConfiguration().getLastRunTimestampPrefix(), currentTime);

                    List<TCID> totalIds = this.challengeFeederManager.getChangedChallengeIds(new java.sql.Date(lastRunTimestamp.getTime()));

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
                        param.setType(this.config.getRedissonConfiguration().getChallengesType());
                        param.setChallengeIds(sub);
                        try {
                            this.challengeFeederManager.pushChallengeFeeder(param);
                        } catch (SupplyException e) {
                            logger.error("Fail to push challenge", e);
                        }

                        from = to;
                    }
                } finally {
                    lock.unlock();
                }
            } else {
                logger.warn("the previous job is still running");
            }
        } catch (Exception exp) {
            throw new JobExecutionException("Error occurs when executing the job", exp);
        } finally {
            if (redisson != null) {
                redisson.shutdown();
            }
        }
    }

}
