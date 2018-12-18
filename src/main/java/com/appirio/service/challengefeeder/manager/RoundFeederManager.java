/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.manager;

import com.appirio.service.challengefeeder.api.RoundComponentData;
import com.appirio.service.challengefeeder.api.RoundData;
import com.appirio.service.challengefeeder.dao.RoundFeederDAO;
import com.appirio.service.challengefeeder.dto.DataScienceFeederParam;
import com.appirio.supply.SupplyException;
import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.request.QueryParameter;
import io.searchbox.client.JestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * RoundFeederManager is used to handle the round data feeder
 * 
 * It's added in TC Elasticsearch feeder - Add Job For populating rounds index v1.0
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
public class RoundFeederManager {
    /**
     * DAO to access round data from the database.
     */
    private final RoundFeederDAO roundFeederDAO;

    /**
     * The jestClient field
     */
    private final JestClient jestClient;

    /**
     * Create RoundFeederManager
     *
     * @param jestClient the jestClient to use
     * @param roundFeederDAO the roundFeederDAO to use
     */
    public RoundFeederManager(JestClient jestClient, RoundFeederDAO roundFeederDAO) {
        this.jestClient = jestClient;
        this.roundFeederDAO = roundFeederDAO;
    }
    
    /**
     * Push round feeder
     *
     * @param param the feeder param to use
     * @throws SupplyException if any error occurs
     */
    public void pushRoundFeeder(DataScienceFeederParam param) throws SupplyException {
        List<Long> roundIds = param.getRoundIds();
        // build query string to filter on round ids
        QueryParameter queryParameter = DataScienceHelper.buildInQueryParameter(roundIds, "roundIds");

        // fetch round data from persistence
        List<RoundData> rounds = this.roundFeederDAO.getRounds(queryParameter);
        for (RoundData round : rounds) {
            round.setStatus(round.getStatus().trim());
            round.setSubTrack(round.getSubTrack().trim());
        }

        // check if all rounds with given roundIds exist
        DataScienceHelper.checkDataScienceExist(roundIds, rounds);

        if (rounds.size() == 0) {
            return;
        }

        List<Map<String, Object>> userIds = this.roundFeederDAO.getUserIds(queryParameter);
        
        // associate users data to the SRMs
        DataScienceHelper.associateDataScienceUsers(rounds, userIds);
        
        // associate components for the rounds
        List<RoundComponentData> components = this.roundFeederDAO.getRoundComponents(queryParameter);
        for (RoundComponentData component : components) {
            for (RoundData round : rounds) {
                if (round.getRoundId().equals(component.getRoundId())) {
                    List<RoundComponentData> temp = round.getComponents();
                    if (temp == null) {
                        temp = new ArrayList<>();
                        round.setComponents(temp);
                    }
                    temp.add(component);
                    component.setRoundId(null);
                }
            }
        }

        // push SRM data to ElasticSearch
        DataScienceHelper.pushDataScience(jestClient, param, rounds);
    }

    /**
     * Get the rounds whose registration phase started after the specified date and after the last run timestamp.
     * 
     * @param date The date param.
     * @param lastRun The last run timestamp.
     * @return The list of TCID.
     */
    public List<TCID> getMatchesWithRegistrationPhaseStartedIds(java.sql.Date date, long lastRun) {
        if (date == null) {
            throw new IllegalArgumentException("The date param should be non-null.");
        }
        return this.roundFeederDAO.getMatchesWithRegistrationPhaseStartedIds(date, lastRun);
    }
}
