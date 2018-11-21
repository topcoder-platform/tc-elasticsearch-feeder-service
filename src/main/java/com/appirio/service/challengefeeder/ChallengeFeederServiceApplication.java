/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder;

import com.appirio.service.BaseApplication;
import com.appirio.service.challengefeeder.job.BaseJob;
import com.appirio.service.challengefeeder.job.LegacyMMToChallengeListingJob;
import com.appirio.service.challengefeeder.job.LoadChangedChallengesDetailJob;
import com.appirio.service.challengefeeder.job.LoadChangedChallengesListingJob;
import com.appirio.service.challengefeeder.job.LoadChangedMMChallengeDetailJob;
import com.appirio.service.challengefeeder.job.MarathonMatchesJob;
import com.appirio.service.challengefeeder.job.SingleRoundMatchesJob;
import com.appirio.service.challengefeeder.resources.HealthCheckResource;
import com.appirio.service.challengefeeder.util.JestClientUtils;
import com.appirio.service.resourcefactory.ChallengeFeederFactory;
import com.appirio.service.resourcefactory.MarathonMatchFeederFactory;
import com.appirio.service.resourcefactory.MmFeederResourceFactory;
import com.appirio.service.resourcefactory.SRMFeederFactory;
import com.appirio.service.supply.resources.SupplyDatasourceFactory;
import de.spinscale.dropwizard.jobs.JobsBundle;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.searchbox.client.JestClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
 * <p>
 * Version 1.4 - Topcoder ElasticSearch Feeder Service - Way To Populate Challenge-Listing Index v1.0
 * <ul>
 * <li>add job for the challenges listing.</li>
 * </ul>
 * </p>
 * <p>
 * Changes in v1.5 (Topcoder - Create CronJob For Populating Marathon Matches and SRMs To Elasticsearch v1.0):
 * <ul>
 * <li>Added LegacyMMToChallengeListingJob for schedule.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Version 1.6 - Topcoder ElasticSearch Feeder Service - Way To Populate Challenge-Detail Index For Legacy Marathon Matches v1.0
 * <ul>
 * <li>add job LoadChangedMMChallengeDetailJob for schedule.</li>
 * </ul>
 * </p>
 * <p>
 * Changes in v 1.7 (Topcoder ElasticSearch Feeder Service - Way To Populate Challenge-Detail Index)
 * <ul>
 *      <li>Add job for LoadChangedChallengeDetailJob</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Version 1.8 - Topcoder Elasticsearch Feeder Service - Jobs Cleanup And Improvement v1.0
 * - remove the useless resources, jobs and logging of configuration values
 * </p>
 * 
 * <p>
 * Version 1.9 - Implement Endpoint To Populate Elasticsearch For The Given Challenges
 * - Changes the challenge resource object creation logic/
 * </p>
 * @author TCSCODER
 * @version 1.9
 */
public class ChallengeFeederServiceApplication extends BaseApplication<ChallengeFeederServiceConfiguration> {
    private static final String PROP_KEY_JWT_SECRET = "TC_JWT_KEY";
    private static final String PROP_KEY_VALID_ISSUERS = "VALID_ISSUERS";

    private static String getProperty(String propertyKey) {
        String key = System.getenv(propertyKey);
        if (key != null) {
            return key;
        }
        key = System.getProperty(propertyKey);
        if (key == null) {
            logger.warn(
                    propertyKey + " is not found in both of environment variables and system properties.");
        }
        return key;
    }


    /**
     * Refer to APIApplication
     */
    @Override
    public String getName() {
        return "challenge-feeder-service";
    }

    /**
     * Override the get secret method.
     * @return the secret value
     */
    @Override
    public String getSecret() {
        return getProperty(PROP_KEY_JWT_SECRET);
    }
    /**
     * Override the get valid issues method.
     * @return the issues value
     */
    @Override
    public List<String> getValidIssuers() {
        // Read valid issuers from env
        List<String> validIssuers = null;
        String validIssuersStr = getProperty(PROP_KEY_VALID_ISSUERS);
        if (validIssuersStr != null) {
            validIssuers = Arrays.asList(validIssuersStr.split(","));
        }
        if (validIssuers == null) {
            validIssuers = new ArrayList<>();
        }
        return validIssuers;
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
        
        logger.info("\tJobsConfiguration");
        logger.info("\t\tRedissonConfiguration");
        logger.info("\t\t\tSingle server address: " + config.getJobsConfiguration().getRedissonConfiguration().getSingleServerAddress());
        logger.info("\t\t\tCluster enabled: " + config.getJobsConfiguration().getRedissonConfiguration().isClusterEnabled());
        logger.info("\t\t\tLock watchdog timeout: " + config.getJobsConfiguration().getRedissonConfiguration().getLockWatchdogTimeout());
        logger.info("\t\t\tNode addresses: " + config.getJobsConfiguration().getRedissonConfiguration().getNodeAddresses());
        
        
        logger.info("\tJobs configuration");
        logger.info("\t\t\tRedisson configuration");
        logger.info("\t\t\t\t\tLock watchdog timeout : " + config.getJobsConfiguration().getRedissonConfiguration().getLockWatchdogTimeout());
        logger.info("\t\t\t\t\tSingle server address : " + config.getJobsConfiguration().getRedissonConfiguration().getSingleServerAddress());
        logger.info("\t\t\t\t\tCluster enabled : " + config.getJobsConfiguration().getRedissonConfiguration().isClusterEnabled());
        logger.info("\t\t\tNode addresses: " + config.getJobsConfiguration().getRedissonConfiguration().getNodeAddresses());
        logger.info("\t\t\t\t\tNode addresses");
        logger.info("\t\t\tLoad changed challenges listing job");
        logger.info("\t\t\t\t\tIndex name");
        logger.info("\t\t\t\t\tBatch size : " + config.getJobsConfiguration().getLoadChangedChallengesListingJob().getBatchUpdateSize());
        logger.info("\t\t\tLoad changed challenges detail job");
        logger.info("\t\t\t\t\tIndex name");
        logger.info("\t\t\t\t\tBatch size : " + config.getJobsConfiguration().getLoadChangedChallengesDetailJob().getBatchUpdateSize());
        logger.info("\t\t\tLegacymm to challenge listing job");
        logger.info("\t\t\t\t\tIndex name");
        logger.info("\t\t\t\t\tBatch size : " + config.getJobsConfiguration().getLegacyMMToChallengeListingJob().getBatchUpdateSize());
        logger.info("\t\t\t\t\tMarathon matches days to subtract : " + config.getJobsConfiguration().getLegacyMMToChallengeListingJob().getMarathonMatchesDaysToSubtract());
        logger.info("\t\t\t\t\tMarathon matches forum url : " + config.getJobsConfiguration().getLegacyMMToChallengeListingJob().getMarathonMatchesForumUrl());
        logger.info("\t\t\tLoad changedmm challenge detail job");
        logger.info("\t\t\t\t\tIndex name");
        logger.info("\t\t\t\t\tBatch size : " + config.getJobsConfiguration().getLoadChangedMMChallengeDetailJob().getBatchUpdateSize());
        logger.info("\t\t\t\t\tMarathon matches days to subtract : " + config.getJobsConfiguration().getLoadChangedMMChallengeDetailJob().getMarathonMatchesDaysToSubtract());
        logger.info("\t\t\t\t\tMarathon matches forum url : " + config.getJobsConfiguration().getLoadChangedMMChallengeDetailJob().getMarathonMatchesForumUrl());
        logger.info("\t\t\tMarathon matches job");
        logger.info("\t\t\t\t\tIndex name");
        logger.info("\t\t\t\t\tBatch size : " + config.getJobsConfiguration().getMarathonMatchesJob().getBatchUpdateSize());
        logger.info("\t\t\t\t\tMarathon matches days to subtract : " + config.getJobsConfiguration().getMarathonMatchesJob().getMarathonMatchesDaysToSubtract());
        logger.info("\t\t\t\t\tMarathon matches forum url : " + config.getJobsConfiguration().getMarathonMatchesJob().getMarathonMatchesForumUrl());
        logger.info("\t\t\tSingle round matches job");
        logger.info("\t\t\t\t\tIndex name");
        logger.info("\t\t\t\t\tBatch size : " + config.getJobsConfiguration().getSingleRoundMatchesJob().getBatchUpdateSize());
        logger.info("\t\t\t\t\tSingle round matches days to subtract : " + config.getJobsConfiguration().getSingleRoundMatchesJob().getSingleRoundMatchesDaysToSubtract());


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
        env.jersey().register(new HealthCheckResource());
        env.jersey().register(new ChallengeFeederFactory(jestClient,config).getResourceInstance());
        env.jersey().register(new MmFeederResourceFactory(jestClient).getResourceInstance());
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

        bootstrap.addBundle(new JobsBundle(new LoadChangedChallengesListingJob(), new LegacyMMToChallengeListingJob(), new MarathonMatchesJob(),
                new SingleRoundMatchesJob(), new LoadChangedChallengesDetailJob(), new LoadChangedMMChallengeDetailJob()));
    }
}
