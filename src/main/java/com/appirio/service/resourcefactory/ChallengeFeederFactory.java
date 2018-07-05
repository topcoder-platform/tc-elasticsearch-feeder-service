package com.appirio.service.resourcefactory;

import com.appirio.service.challengefeeder.ChallengeFeederServiceConfiguration;
import com.appirio.service.challengefeeder.dao.ChallengeDetailFeederDAO;
import com.appirio.service.challengefeeder.dao.ChallengeListingFeederDAO;
import com.appirio.service.challengefeeder.manager.ChallengeDetailFeederManager;
import com.appirio.service.challengefeeder.manager.ChallengeListingFeederManager;
import com.appirio.service.challengefeeder.resources.ChallengeFeederResource;
import com.appirio.service.challengefeeder.util.JestClientUtils;
import com.appirio.service.supply.resources.ResourceFactory;
import com.appirio.supply.DAOFactory;
import com.appirio.supply.SupplyException;
import io.searchbox.client.JestClient;

/**
 * Factory for ChallengeFeederResource
 * 
 * Version 1.1 - Topcoder Elasticsearch Feeder Service - Jobs Cleanup And Improvement v1.0
 * - create dummy resource
 *
 * Version 1.2 - Implement Endpoint To Populate Elasticsearch For The Given Challenges
 * - Changes the challenge factory object creation logic
 * 
 * @author TCSCODER
 * @version 1.2
 */
public class ChallengeFeederFactory implements ResourceFactory<ChallengeFeederResource> {

    /**
     * The jest client
     */
    private final JestClient jestClient;
    
    private final ChallengeFeederServiceConfiguration config;

    /**
     * Simple constructor
     * 
     * @param jestClient the jest client
     */
    public ChallengeFeederFactory(JestClient jestClient,ChallengeFeederServiceConfiguration config) {
        this.jestClient = jestClient;
        this.config = config;
    }

    /**
     * Get ChallengeFeederResource object
     *
     * @return ChallengeFeederResource the challenge feeder resource
     * @throws SupplyException exception for supply server
     */
	@Override
	public ChallengeFeederResource getResourceInstance() throws SupplyException {
		final ChallengeListingFeederManager challengeListingFeederManager = new ChallengeListingFeederManager(
				JestClientUtils.get(this.config.getJestClientConfiguration()),
				DAOFactory.getInstance().createDAO(ChallengeListingFeederDAO.class),
				this.config.getChallengeConfiguration());
		final ChallengeDetailFeederManager challengeDetailFeederManager= new ChallengeDetailFeederManager(JestClientUtils.get(this.config.getJestClientConfiguration()),
				DAOFactory.getInstance().createDAO(ChallengeDetailFeederDAO.class));
        return new ChallengeFeederResource(challengeDetailFeederManager,challengeListingFeederManager,config);
    }
}
