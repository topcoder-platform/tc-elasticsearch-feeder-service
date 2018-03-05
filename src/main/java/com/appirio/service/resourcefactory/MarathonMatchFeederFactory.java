/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.resourcefactory;

import com.appirio.service.challengefeeder.dao.MarathonMatchFeederDAO;
import com.appirio.service.challengefeeder.manager.MarathonMatchFeederManager;
import com.appirio.service.challengefeeder.resources.MarathonMatchFeederResource;
import com.appirio.service.supply.resources.ResourceFactory;
import com.appirio.supply.DAOFactory;
import com.appirio.supply.SupplyException;

import io.searchbox.client.JestClient;


/**
 * Factory for MarathonMatchFeederResource.
 * <p>
 * Added in Topcoder - Add Endpoints To Populating Marathon Matches And SRMs Into Elasticsearch v1.0
 * </p>
 *
 * @author TCCoder
 * @version 1.0
 */
public class MarathonMatchFeederFactory implements ResourceFactory<MarathonMatchFeederResource> {

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
    public MarathonMatchFeederFactory(JestClient jestClient) {
        this.jestClient = jestClient;
    }

    /**
     * Get MarathonMatchFeederResource object
     *
     * @return MarathonMatchFeederResource the marathon match feeder resource
     * @throws SupplyException
     *             exception for supply server
     */
    @Override
    public MarathonMatchFeederResource getResourceInstance() throws SupplyException {
        final MarathonMatchFeederManager marathonMatchFeederManager = new MarathonMatchFeederManager(jestClient,
            DAOFactory.getInstance().createDAO(MarathonMatchFeederDAO.class));

        return new MarathonMatchFeederResource(marathonMatchFeederManager);
    }
}
