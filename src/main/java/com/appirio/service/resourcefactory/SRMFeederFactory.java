/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.resourcefactory;

import com.appirio.service.challengefeeder.dao.SRMFeederDAO;
import com.appirio.service.challengefeeder.manager.SRMFeederManager;
import com.appirio.service.challengefeeder.resources.SRMFeederResource;
import com.appirio.service.supply.resources.ResourceFactory;
import com.appirio.supply.DAOFactory;
import com.appirio.supply.SupplyException;

import io.searchbox.client.JestClient;


/**
 * Factory for SRMFeederResource.
 * <p>
 * Added in Topcoder - Add Endpoints To Populating Marathon Matches And SRMs Into Elasticsearch v1.0
 * </p>
 *
 * @author TCCoder
 * @version 1.0
 */
public class SRMFeederFactory implements ResourceFactory<SRMFeederResource> {

    /**
     * The jest client
     */
    private final JestClient jestClient;

    /**
     * Simple constructor
     *
     * @param jestClient
     *            the jest client
     */
    public SRMFeederFactory(JestClient jestClient) {
        this.jestClient = jestClient;
    }

    /**
     * Get SRMFeederResource object
     *
     * @return SRMFeederResource the SRM feeder resource
     * @throws SupplyException
     *             exception for supply server
     */
    @Override
    public SRMFeederResource getResourceInstance() throws SupplyException {
        final SRMFeederManager srmFeederManager = new SRMFeederManager(jestClient, DAOFactory.getInstance().createDAO(
            SRMFeederDAO.class));

        return new SRMFeederResource(srmFeederManager);
    }
}
