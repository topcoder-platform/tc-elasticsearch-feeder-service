/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.resourcefactory;

import com.appirio.service.challengefeeder.dao.MmFeederDAO;
import com.appirio.service.challengefeeder.manager.MmFeederManager;
import com.appirio.service.challengefeeder.resources.MmFeederResource;
import com.appirio.service.supply.resources.ResourceFactory;
import com.appirio.supply.DAOFactory;
import com.appirio.supply.SupplyException;
import io.searchbox.client.JestClient;

/**
 * Factory for MmFeederResource
 *
 * It's added in Topcoder - Populate Marathon Match Related Data Into Challenge Model In Elasticsearch v1.0
 *  
 * @author TCSCODER
 * @version 1.0
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
        final MmFeederManager mmFeederManager = new MmFeederManager(jestClient, DAOFactory.getInstance().createDAO(MmFeederDAO.class));
        return new MmFeederResource(mmFeederManager);
    }
}