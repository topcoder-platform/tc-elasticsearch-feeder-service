package com.appirio.service.resourcefactory;

import com.appirio.service.challengefeeder.dao.ChallengeFeederDAO;
import com.appirio.service.challengefeeder.manager.ChallengeFeederManager;
import com.appirio.service.challengefeeder.resources.ChallengeFeederResource;
import com.appirio.service.supply.resources.ResourceFactory;
import com.appirio.supply.DAOFactory;
import com.appirio.supply.SupplyException;
import io.searchbox.client.JestClient;

/**
 * Factory for ChallengeFeederResource
 *
 * 
 * @author TCSCODER
 * @version 1.0
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
        final ChallengeFeederManager challengeManager = new ChallengeFeederManager(jestClient, DAOFactory.getInstance().createDAO(ChallengeFeederDAO.class));

        return new ChallengeFeederResource(challengeManager);
    }
}
