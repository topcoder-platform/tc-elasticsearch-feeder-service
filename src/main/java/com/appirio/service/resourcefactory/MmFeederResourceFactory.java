/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.resourcefactory;

import com.appirio.service.challengefeeder.resources.MmFeederResource;
import com.appirio.service.supply.resources.ResourceFactory;
import com.appirio.supply.SupplyException;
import io.searchbox.client.JestClient;

/**
 * Factory for MmFeederResource
 *
 * It's added in Topcoder - Populate Marathon Match Related Data Into Challenge Model In Elasticsearch v1.0
 * 
 * Version 1.1 - Topcoder Elasticsearch Feeder Service - Jobs Cleanup And Improvement v1.0
 * - create dummy resource
 * 
 *  
 * @author TCSCODER
 * @version 1.1 
 */
public class MmFeederResourceFactory implements ResourceFactory<MmFeederResource> {

    /**
     * The jest client
     */
    private final JestClient jestClient;

    /**
     * Simple constructor
     * 
     * @param jestClient the jest client
     */
    public MmFeederResourceFactory(JestClient jestClient) {
        this.jestClient = jestClient;
    }

    /**
     * Get resource instance
     *
     * @throws SupplyException if any error occurs
     * @return the MmFeederResource result
     */
    @Override
    public MmFeederResource getResourceInstance() throws SupplyException {
        return new MmFeederResource();
    }
}