/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.job;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appirio.service.challengefeeder.dao.SRMFeederDAO;
import com.appirio.service.challengefeeder.dto.DataScienceFeederParam;
import com.appirio.service.challengefeeder.manager.SRMFeederManager;
import com.appirio.service.challengefeeder.util.JestClientUtils;
import com.appirio.supply.DAOFactory;
import com.appirio.tech.core.api.v3.TCID;

import de.spinscale.dropwizard.jobs.annotations.DelayStart;
import de.spinscale.dropwizard.jobs.annotations.Every;
import lombok.NoArgsConstructor;

/**
 * SingleRoundMatchesJob is used to populate single round matches matches to elasticsearch.
 * 
 * It was added in Topcoder - Create CronJob For Populating Marathon Matches and SRMs To Elasticsearch v1.0
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
@DelayStart("25s")
@Every("${com.appirio.service.challengefeeder.job.SingleRoundMatchesJob}")
@NoArgsConstructor
public class SingleRoundMatchesJob extends BaseJob {
    
    /**
     * The single round match feeder manager instance.
     */
    private SRMFeederManager manager;

    /**
     * Logger used to log events
     */
    private static final Logger logger = LoggerFactory.getLogger(SingleRoundMatchesJob.class);

    /**
     * Create a SingleRoundMatchesJob.
     * @param manager the srm feeder manager
     */
    public SingleRoundMatchesJob(SRMFeederManager manager) {
        this.manager = manager;
    }

    /**
     * Do job. Load the single round matches to elasticsearch service.
     *
     * @param context the job context to use
     * @throws JobExecutionException if any error occurs
     */
    @Override
    public void doJob(JobExecutionContext context) throws JobExecutionException {
        RLock lock;
        RedissonClient redisson = null;
        try {
            if (this.manager == null) {
                this.manager = new SRMFeederManager(JestClientUtils.get(GLOBAL_CONFIGURATION.getJestClientConfiguration()), DAOFactory.getInstance().createDAO(SRMFeederDAO.class));
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

            logger.info("Try to get the lock for single algorithm matches job");
            redisson = Redisson.create(redissonConfig);
            lock = redisson.getLock(config.getRedissonConfiguration().getSingleRoundMatchesJobLockerKeyName());
            if (lock.tryLock()) {
                logger.info("Get the lock for single algorithm matches job successfully");
                try {
                    RMapCache<String, String> mapCache = redisson.getMapCache(config.getRedissonConfiguration().getSingleRoundMatchesJobLastRunTimestampPrefix());
                    String timestamp = mapCache.get(config.getRedissonConfiguration().getSingleRoundMatchesJobLastRunTimestampPrefix());

                    Date lastRunTimestamp = new Date(INITIAL_TIMESTAMP);
                    if (timestamp != null) {
                        lastRunTimestamp = DATE_FORMAT.parse(timestamp);
                    }

                    logger.info("The last run timestamp for single algorithm matches job is:" + lastRunTimestamp.getTime());

                    Date currentTimestamp = this.manager.getTimestamp();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(currentTimestamp);
                    calendar.add(Calendar.DAY_OF_MONTH, this.config.getRedissonConfiguration().getSingleRoundMatchesDaysToSubtract());
                    Date dateParam = calendar.getTime();

                    logger.info("The initial timestamp for single algorithm matches job is:" + dateParam);

                    List<TCID> totalIds = this.manager.getMatchesWithRegistrationPhaseStartedIds(new java.sql.Date(dateParam.getTime()), lastRunTimestamp.getTime());

                    List<Long> ids = new ArrayList<Long>();
                    for (int i = 0; i < totalIds.size(); ++i) {
                        ids.add(Long.parseLong(totalIds.get(i).getId()));
                    }
                    logger.info("The count of the SRM ids to load:" + ids.size());
                    logger.info("The SRM ids to load:" + ids);

                    int batchSize = this.config.getRedissonConfiguration().getBatchUpdateSize();
                    int to = 0;
                    int from = 0;
                    while (to < ids.size()) {
                        to += (to + batchSize) > ids.size() ? (ids.size() - to) : batchSize;
                        List<Long> sub = ids.subList(from, to);
                        DataScienceFeederParam param = new DataScienceFeederParam();

                        param.setIndex(this.config.getRedissonConfiguration().getSrmsIndex());
                        param.setType(this.config.getRedissonConfiguration().getSrmsType());
                        param.setRoundIds(sub);
                        try {
                            this.manager.pushSRMFeeder(param);
                        } catch(Exception e) {
                           e.printStackTrace();
                        }

                        from = to;
                    }

                    // mark last execution as current timestamp
                    logger.info("update last run timestamp for challenges job is:" + currentTimestamp);
                    mapCache.put(config.getRedissonConfiguration().getSingleRoundMatchesJobLastRunTimestampPrefix(), DATE_FORMAT.format(currentTimestamp));
                } finally {
                    logger.info("release the lock for single algorithm matches job");
                    lock.unlock();
                }
            } else {
                logger.warn("the previous job for single algorithm matches job is still running");
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
