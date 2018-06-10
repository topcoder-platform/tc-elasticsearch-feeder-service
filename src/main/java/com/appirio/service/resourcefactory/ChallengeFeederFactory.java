package com.appirio.service.resourcefactory;

import com.appirio.service.challengefeeder.resources.ChallengeFeederResource;
import com.appirio.service.supply.resources.ResourceFactory;
import com.appirio.supply.SupplyException;
import io.searchbox.client.JestClient;

/**
 * Factory for ChallengeFeederResource
 * 
 * Version 1.1 - Topcoder Elasticsearch Feeder Service - Jobs Cleanup And Improvement v1.0
 * - create dummy resource
 * 
 *
 * 
 * @author TCSCODER
 * @version 1.1 
 */
public class ChallengeFeederFactory implements ResourceFactory<ChallengeFeederResource> {

    /**
     * The jest client
     */
    private final JestClient jestClient;

    /**
     * Simple constructor
     * 
     * @param jestClient the jest client
     */
    public ChallengeFeederFactory(JestClient jestClient) {
        this.jestClient = jestClient;
    }

    /**
     * Get ChallengeFeederResource object
     *
     * @return ChallengeFeederResource the challenge feeder resource
     * @throws SupplyException exception for supply server
     */
    @Override
    public ChallengeFeederResource getResourceInstance() throws SupplyException {
        return new ChallengeFeederResource();
    }
}
