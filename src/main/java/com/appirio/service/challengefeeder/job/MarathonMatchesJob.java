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

import com.appirio.service.challengefeeder.ChallengeFeederServiceConfiguration;
import com.appirio.service.challengefeeder.dao.MarathonMatchFeederDAO;
import com.appirio.service.challengefeeder.dto.DataScienceFeederParam;
import com.appirio.service.challengefeeder.manager.MarathonMatchFeederManager;
import com.appirio.service.challengefeeder.util.JestClientUtils;
import com.appirio.supply.DAOFactory;
import com.appirio.tech.core.api.v3.TCID;

import de.spinscale.dropwizard.jobs.annotations.DelayStart;
import de.spinscale.dropwizard.jobs.annotations.Every;
import lombok.NoArgsConstructor;

/**
 * MarathonMatchesJob is used to populate marathon matches to elasticsearch.
 * 
 * It was added in Topcoder - Create CronJob For Populating Marathon Matches and SRMs To Elasticsearch v1.0
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
@DelayStart("20s")
@Every("${com.appirio.service.challengefeeder.job.MarathonMatchesJob}")
@NoArgsConstructor
public class MarathonMatchesJob extends BaseJob {

    /**
     * The marathon match feeder manager instance.
     */
    private MarathonMatchFeederManager manager;

    /**
     * Logger used to log events
     */
    private static final Logger logger = LoggerFactory.getLogger(MarathonMatchesJob.class);

    /**
     * MarathonMatchesJob constructor.
     *
     * @param manager the MarathonMatchFeederManager to use
     * @param config the config to use
     */
    public MarathonMatchesJob(MarathonMatchFeederManager manager, ChallengeFeederServiceConfiguration config) {
        super(config);
        this.manager = manager;
    }

    /**
     * Do job. Load the marathon matches to elasticsearch service.
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
                this.manager = new MarathonMatchFeederManager(JestClientUtils.get(GLOBAL_CONFIGURATION.getJestClientConfiguration()), DAOFactory.getInstance().createDAO(MarathonMatchFeederDAO.class));
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
            lock = redisson.getLock(config.getRedissonConfiguration().getMarathonMatchesJobLockerKeyName());
            lock.lock();
            logger.info("Get the lock successfully");
            
            RMapCache<String, String> mapCache = redisson.getMapCache(config.getRedissonConfiguration().getMarathonMatchesJobLastRunTimestampPrefix());
            String timestamp = mapCache.get(config.getRedissonConfiguration().getMarathonMatchesJobLastRunTimestampPrefix());
            
            Date lastRunTimestamp = new Date(INITIAL_TIMESTAMP);
            if (timestamp != null) {
                lastRunTimestamp = DATE_FORMAT.parse(timestamp);
            }
            
            logger.info("The last run timestamp is:" + lastRunTimestamp.getTime());

            Date currentTimestamp = this.manager.getTimestamp();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentTimestamp);
            calendar.add(Calendar.DAY_OF_MONTH, this.config.getRedissonConfiguration().getMarathonMatchesDaysToSubtract());
            Date dateParam = calendar.getTime();
            
            logger.info("The initial timestamp is:" + dateParam);
            
            List<TCID> totalIds = this.manager.getMatchesWithRegistrationPhaseStartedIds(new java.sql.Date(dateParam.getTime()), lastRunTimestamp.getTime());

            List<Long> ids = new ArrayList<Long>();
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
                DataScienceFeederParam param = new DataScienceFeederParam();
                param.setIndex(this.config.getRedissonConfiguration().getMmIndex());
                param.setType(this.config.getRedissonConfiguration().getMmType());
                param.setRoundIds(sub);
                this.manager.pushMarathonMatchFeeder(param);
                from = to;
            }

            // mark last execution as current timestamp
            mapCache.put(config.getRedissonConfiguration().getMarathonMatchesJobLastRunTimestampPrefix(), DATE_FORMAT.format(currentTimestamp));
            
            lock.unlock();
        } catch (Exception exp) {
            throw new JobExecutionException("Error occurs when executing the job", exp);
        } finally {
            if (lock != null) {
                lock.expire(100, TimeUnit.MILLISECONDS);
                
            }
            if (redisson != null) {
                redisson.shutdown();
            }
        }
    }

}
