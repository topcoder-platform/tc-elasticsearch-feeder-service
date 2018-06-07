/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.dao;

import com.appirio.service.challengefeeder.api.challengelisting.ChallengeListingData;
import com.appirio.service.challengefeeder.api.challengelisting.WinnerData;
import com.appirio.supply.dataaccess.ApiQueryInput;
import com.appirio.supply.dataaccess.DatasourceName;
import com.appirio.supply.dataaccess.SqlQueryFile;
import com.appirio.tech.core.api.v3.request.QueryParameter;

import java.util.List;
import java.util.Map;

/**
 * DAO to interact with marathon match data
 *
 * It's added in Topcoder ElasticSearch Feeder Service - Way To Populate Challenge-Listing Index For Legacy Marathon Matches v1.0
 * 
 * @author TCCODER
 * @version 1.0
 */
@DatasourceName("oltp")
public interface ChallengeListingMMFeederDAO {
    /**
     * Get marathon matches
     *
     * @param queryParameter the queryParameter to use
     * @return the List<ChallengeData> result
     */
    @SqlQueryFile("sql/mm-feeder-into-challenges/get_marathon_matches_for_challenge_listing.sql")
    List<ChallengeListingData> getMarathonMatches(@ApiQueryInput QueryParameter queryParameter);
    
    /**
     * Get marathon match winners 
     *
     * @param queryParameter the queryParameter to use
     * @return the List<WinnerData> result
     */
    @SqlQueryFile("sql/mm-feeder-into-challenges/get_marathon_match_winners.sql")
    List<WinnerData> getMarathonMatchWinners(@ApiQueryInput QueryParameter queryParameter);

    /**
     * Get id of all submitters
     *
     * @param queryParameter query parameter
     * @return list of submitter
     */
    @SqlQueryFile("sql/mm-feeder-into-challenges/get_submitter_ids.sql")
    List<Map<String, Object>> getSubmitterIds(@ApiQueryInput QueryParameter queryParameter);
}
