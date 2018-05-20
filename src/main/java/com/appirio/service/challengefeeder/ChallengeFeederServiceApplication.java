/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder;

import com.appirio.service.BaseApplication;
import com.appirio.service.challengefeeder.job.*;
import com.appirio.service.challengefeeder.resources.HealthCheckResource;
import com.appirio.service.challengefeeder.util.JestClientUtils;
import com.appirio.service.resourcefactory.ChallengeFeederFactory;
import com.appirio.service.resourcefactory.MmFeederResourceFactory;
import com.appirio.service.resourcefactory.MarathonMatchFeederFactory;
import com.appirio.service.resourcefactory.SRMFeederFactory;
import com.appirio.service.supply.resources.SupplyDatasourceFactory;

import de.spinscale.dropwizard.jobs.JobsBundle;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.searchbox.client.JestClient;

/**
 * The entry point for challenge feeder micro service
 * <p>
 * Version 1.1 - Topcoder - Populate Marathon Match Related Data Into Challenge Model In Elasticsearch v1.0
 * - register the MmFeederResource
 * </p>
 *
 * <p>
 * Version 1.2 - Topcoder - Create CronJob For Populating Changed Challenges To Elasticsearch v1.0
 * - initialize the cron job bundle
 * </p>
 *
 * <p>
 * Changes in v1.3 (Topcoder - Add Endpoints To Populating Marathon Matches And SRMs Into Elasticsearch v1.0):
 * <ul>
 * <li>Added resources for Marathon Matches and SRMs.</li>
 * </ul>
 * </p>
 * <p>
 * Changes in v1.3 (Topcoder - Create CronJob For Populating Marathon Matches and SRMs To Elasticsearch v1.0):
 * <ul>
 * <li>Added job for MarathonMatchesJob.</li>
 * <li>Added job for SingleRoundMatchesJob.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Version 1.4 - Topcoder ElasticSearch Feeder Service - Way To Populate Challenge-Listing Index v1.0
 * <ul>
 * <li>add job for the challenges listing.</li>
 * </ul>
 * </p>
 *
 * @author TCSCODER
 * @version 1.4 
 */
public class ChallengeFeederServiceApplication extends BaseApplication<ChallengeFeederServiceConfiguration> {
    /**
     * Refer to APIApplication
     */
    @Override
    public String getName() {
        return "challenge-feeder-service";
    }

    /**
     * Log service specific configuration values.
     *
     * @param config the configuration
     */
    @Override
    protected void logServiceSpecificConfigs(ChallengeFeederServiceConfiguration config) {
        for (SupplyDatasourceFactory dbConfig : config.getDatabases()) {
            logger.info("\tJDBI configuration ");
            logger.info("\t\tDatabase config name : " + dbConfig.getDatasourceName());
            logger.info("\t\tOLTP driver class : " + dbConfig.getDriverClass());
            logger.info("\t\tOLTP connection URL : " + dbConfig.getUrl());
            logger.info("\t\tOLTP Authentication user : " + dbConfig.getUser());
        }

        logger.info("\tJestClient configuration");
        logger.info("\t\tElasticSearch URL : " + config.getJestClientConfiguration().getElasticSearchUrl());
        logger.info("\t\tChallenges Index name : " + config.getJestClientConfiguration().getChallengesIndexName());
        logger.info("\t\tMax total connections : " + config.getJestClientConfiguration().getMaxTotalConnections());
        logger.info("\t\tConnection timeout (ms) : " + config.getJestClientConfiguration().getConnTimeout());
        logger.info("\t\tRead timeout (ms) : " + config.getJestClientConfiguration().getReadTimeout());
        logger.info("\t\tAWS signing enabled : " + config.getJestClientConfiguration().isAwsSigningEnabled());
        logger.info("\t\tAWS region : " + config.getJestClientConfiguration().getAwsRegion());
        logger.info("\t\tAWS service : " + config.getJestClientConfiguration().getAwsService());
        
        logger.info("\tRedissonConfiguration ");
        logger.info("\t\tChallenges index: " + config.getRedissonConfiguration().getChallengesIndex());
        logger.info("\t\tChallenges type: " + config.getRedissonConfiguration().getChallengesType());
        logger.info("\t\tSingle server address: " + config.getRedissonConfiguration().getSingleServerAddress());
        logger.info("\t\tLast run timestamp prefix for job LoadChangedChallengesJob: " + config.getRedissonConfiguration().getLoadChangedChallengesJobLastRunTimestampPrefix());
        logger.info("\t\tLast run timestamp prefix for job MarathonMatchesJob: " + config.getRedissonConfiguration().getMarathonMatchesJobLastRunTimestampPrefix());
        logger.info("\t\tLast run timestamp prefix for job SingleRoundMatchesJob: " + config.getRedissonConfiguration().getSingleRoundMatchesJobLastRunTimestampPrefix());
        logger.info("\t\tCluster enabled: " + config.getRedissonConfiguration().isClusterEnabled());
        logger.info("\t\tLoadChangedChallengesJob Locker key name: " + config.getRedissonConfiguration().getLoadChangedChallengesJobLockerKeyName());
        logger.info("\t\tMarathonMatchesJob Locker key name: " + config.getRedissonConfiguration().getMarathonMatchesJobLockerKeyName());
        logger.info("\t\tSingleRoundMatchesJob Locker key name: " + config.getRedissonConfiguration().getSingleRoundMatchesJobLockerKeyName());
        logger.info("\t\tLock watchdog timeout: " + config.getRedissonConfiguration().getLockWatchdogTimeout());
        logger.info("\t\tNode addresses: " + config.getRedissonConfiguration().getNodeAddresses());

        logger.info("\tJobs ");
        logger.info("\t\tJobs: " + config.getJobs());
        logger.info("\r\n");
    }

    /**
     * Application entrypoint. See dropwizard and jetty documentation for more
     * details
     *
     * @param args arguments to main
     * @throws Exception Generic exception
     */
    public static void main(String[] args) throws Exception {
        new ChallengeFeederServiceApplication().run(args);
    }

    /**
     * Gives the subclasses an opportunity to register resources
     *
     * @param config the configuration
     * @param env the environment
     */
    @Override
    protected void registerResources(ChallengeFeederServiceConfiguration config, Environment env) throws Exception {
        // create JestClient
        JestClient jestClient = JestClientUtils.get(config.getJestClientConfiguration());

        // Register resources here
        env.jersey().register(new ChallengeFeederFactory(jestClient).getResourceInstance());
        env.jersey().register(new MmFeederResourceFactory(jestClient).getResourceInstance());
        env.jersey().register(new HealthCheckResource());
        env.jersey().register(new MarathonMatchFeederFactory(jestClient).getResourceInstance());
        env.jersey().register(new SRMFeederFactory(jestClient).getResourceInstance());
        logger.info("Services registered");
        BaseJob.GLOBAL_CONFIGURATION = config;
    }

    /**
     * Gives the subclasses an opportunity to prepare to run
     *
     * @param config the configuration
     * @param env the environment
     */
    @Override
    protected void prepare(ChallengeFeederServiceConfiguration config, Environment env) throws Exception {
        // configure the database
        configDatabases(config, config.getDatabases(), env);
    }

    /**
     * Initialize method
     *
     * @param bootstrap the bootstrap to use
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void initialize(Bootstrap<ChallengeFeederServiceConfiguration> bootstrap) {
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(false)));
        bootstrap.addBundle(new JobsBundle(new StartupJob(), new LoadChangedChallengesJob(), new LoadChangedChallengesListingJob(),
                new MarathonMatchesJob(), new SingleRoundMatchesJob(), new LegacyMMToChallengeJob()));
    }
}
