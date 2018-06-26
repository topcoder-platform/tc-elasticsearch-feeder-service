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

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appirio.service.challengefeeder.ChallengeFeederServiceConfiguration;
import com.appirio.supply.SupplyException;
import com.appirio.tech.core.api.v3.TCID;

import de.spinscale.dropwizard.jobs.Job;

/**
 * BaseJob abstract class used as base class to children job classes.
 * 
 * It was added in Topcoder - Create CronJob For Populating Marathon Matches and SRMs To Elasticsearch v1.0
 * 
 *  Version 2.0 - Topcoder Elasticsearch Feeder Service - Jobs Cleanup And Improvement v1.0
 * - change DATE_FORMAT to non-static variable as SimpleDateFormat is not thread safe
 * - abstract out common logic for the sub classes to implement
 * 
 * 
 * @author TCCoder
 * @version 2.0
 *
 */
public abstract class BaseJob extends Job {
    /**
     * The JOB_ENABLE_SUFFIX field 
     */
    public static final String JOB_ENABLE_SUFFIX = ".job.enable";
    
    /**
     * The JOB_LAST_RUN_TIMESTAMP_SUFFIX field 
     */
    public static final String JOB_LAST_RUN_TIMESTAMP_SUFFIX = ".job.lastrun.timestamp";
    
    /**
     * The JOB_LOCKER_NAME_SUFFIX field 
     */
    public static final String JOB_LOCKER_NAME_SUFFIX = ".job.locker";
            
    /**
     * The GLOBAL_CONFIGURATION field.
     */
    public static ChallengeFeederServiceConfiguration GLOBAL_CONFIGURATION;
    
    /**
     * Logger used to log events
     */
    private static final Logger logger = LoggerFactory.getLogger(BaseJob.class);
    
    /**
     * The initial timestamp constant.
     */
    protected static final long INITIAL_TIMESTAMP = 1L;
    
    /**
     * The DATE_FORMAT field.
     */
    protected final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    /**
     * The config field.
     */
    protected ChallengeFeederServiceConfiguration config;
    
    /**
     * The indexName field 
     */
    protected String indexName;
    
    /**
     * The typeName field 
     */
    protected String typeName;
    
    /**
     * The batchSize field 
     */
    protected int batchSize;
    
    /**
     * The lastRuntimestampeKey field 
     */
    protected String lastRuntimestampeKey;
    
    /**
     * The lockerKey field 
     */
    protected String lockerKey;
    
    /**
     * The jobEnableKey field 
     */
    protected String jobEnableKey;

    /**
     * BaseJob constructor.
     * 
     */
    public BaseJob() {
        this.config = GLOBAL_CONFIGURATION;
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
    
    /**
     * Do job. This methods load the data to elastic services.
     *
     * @param context the context to use
     * @throws JobExecutionException if any error occurs
     */
    @Override
    public void doJob(JobExecutionContext context) throws JobExecutionException {
        RLock lock;
        RedissonClient redisson = null;
        try {
            this.init();
            redisson = this.getRedissonClient();
            
            RMap<String, String> mapCache = redisson.getMap(this.getClass().getName() + ".map.cache");
            
            String enable = mapCache.get(this.jobEnableKey);
            logger.info("The " + this.getClass().getName() + " is enable: " + enable);
            if (null == enable || "true".equalsIgnoreCase(enable)) {
                logger.info("Try to get the lock for " + this.getClass().getName() + " by the locker key: " + this.lockerKey);
                lock = redisson.getLock(this.lockerKey);
                if (lock.tryLock()) {
                    logger.info("Get the lock successfully for " + this.getClass().getName());
                    try {
                        String timestamp = mapCache.get(this.lastRuntimestampeKey);
                        Date lastRunTimestamp = new Date(1L);
                        if (timestamp != null) {
                            lastRunTimestamp = dateFormat.parse(timestamp);
                        }

                        logger.info("The last run timestamp for " + this.getClass().getName() + " is: " + timestamp);

                        Date currentTime = new Date();
                        List<Long> ids = this.getFeederIdsToPush(lastRunTimestamp);
                        logger.info("The count of the ids to load for " + this.getClass().getName() + ":" + ids.size());
                        logger.info("The ids to load for " + this.getClass().getName() + ":" + ids);

                        int to = 0;
                        int from = 0;
                        while (to < ids.size()) {
                            to += (to + batchSize) > ids.size() ? (ids.size() - to) : batchSize;
                            List<Long> sub = ids.subList(from, to);
                            try {
                                logger.info(this.getClass().getName() + " - populate documents for " + ids);
                                this.pushFeeders(sub);
                            } catch (Exception e) {
                                // continue the batch process
                                e.printStackTrace();
                            }
                            from = to;
                        }

                        logger.info("update last run timestamp for " + this.getClass().getName() + " is: " + dateFormat.format(currentTime));
                        mapCache.put(this.lastRuntimestampeKey, dateFormat.format(currentTime));
                    } finally {
                        logger.info("release the lock for " + this.getClass().getName());
                        lock.unlock();
                    }
                } else {
                    logger.warn("the previous " + this.getClass().getName() + " is still running");
                }
            }
            
        } catch (Exception exp) {
            exp.printStackTrace();
        } finally {
            if (redisson != null) {
                redisson.shutdown();
            }
        }
    }
    
    /**
     * Initialize the job.
     * It will be called before the job is executed.
     *
     * @throws SupplyException if any error occurs
     */
    protected void init() throws SupplyException {
        if (this.config == null) {
            this.config = GLOBAL_CONFIGURATION;
        }
    }
    
    /**
     * Push feeders
     *
     * @param ids the ids to use
     * @throws SupplyException if any error occurs
     */
    abstract protected void pushFeeders(List<Long> ids) throws SupplyException ;
    
    /**
     * Get feeder ids to push
     *
     * @param lastRunTimestamp the lastRunTimestamp to use
     * @throws SupplyException if any error occurs
     * @return the List<Long> result
     */
    abstract protected List<Long> getFeederIdsToPush(Date lastRunTimestamp) throws SupplyException ;
    
    /**
     * Get redisson client
     *
     * @return the RedissonClient result
     */
    protected RedissonClient getRedissonClient() {
        Config redissonConfig = new Config();
        redissonConfig.setLockWatchdogTimeout(this.config.getJobsConfiguration().getRedissonConfiguration().getLockWatchdogTimeout());
        if (this.config.getJobsConfiguration().getRedissonConfiguration().isClusterEnabled()) {
            for (String addr : this.config.getJobsConfiguration().getRedissonConfiguration().getNodeAddresses()) {
                redissonConfig.useClusterServers().addNodeAddress(addr);
            }
        } else {
            redissonConfig.useSingleServer().setAddress(this.config.getJobsConfiguration().getRedissonConfiguration().getSingleServerAddress());
        }
        
        return Redisson.create(redissonConfig);
    }
    
    /**
     * Convert TCID
     *
     * @param ids the ids to use
     * @return the List<Long> result
     */
    public static List<Long> convertTCID(List<TCID> ids) {
        if (ids == null) {
            return null;
        }
        List<Long> result = new ArrayList<>();
        for (TCID id : ids) {
            result.add(Long.parseLong(id.getId()));
        }
        
        return result;
    }
}
