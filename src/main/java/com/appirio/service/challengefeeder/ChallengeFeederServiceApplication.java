/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder;

import com.appirio.service.BaseApplication;

import com.appirio.service.challengefeeder.resources.HealthCheckResource;
import com.appirio.service.challengefeeder.util.JestClientUtils;
import com.appirio.service.resourcefactory.ChallengeFeederFactory;
import com.appirio.service.resourcefactory.MmFeederResourceFactory;
import com.appirio.service.resourcefactory.MarathonMatchFeederFactory;
import com.appirio.service.resourcefactory.SRMFeederFactory;
import com.appirio.service.supply.resources.SupplyDatasourceFactory;

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
 * Changes in v1.2 (Topcoder - Add Endpoints To Populating Marathon Matches And SRMs Into Elasticsearch v1.0):
 * <ul>
 * <li>Added resources for Marathon Matches and SRMs.</li>
 * </ul>
 * </p>
 *
 * @author TCSCODER
 * @version 1.2
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
    @Override
    public void initialize(Bootstrap<ChallengeFeederServiceConfiguration> bootstrap) {
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(false)));
    }
}
