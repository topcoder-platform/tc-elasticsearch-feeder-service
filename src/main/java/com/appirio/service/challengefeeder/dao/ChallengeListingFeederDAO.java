/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.dao;

import java.util.List;
import java.util.Map;

import com.appirio.service.challengefeeder.api.UserIdData;
import com.appirio.service.challengefeeder.api.challengelisting.ChallengeListingData;
import com.appirio.service.challengefeeder.api.challengelisting.EventData;
import com.appirio.service.challengefeeder.api.challengelisting.WinnerData;
import com.appirio.supply.dataaccess.ApiQueryInput;
import com.appirio.supply.dataaccess.DatasourceName;
import com.appirio.supply.dataaccess.SqlQueryFile;
import com.appirio.tech.core.api.v3.request.QueryParameter;

/**
 * DAO to interact with challenge data
 * 
 * It's added in Topcoder ElasticSearch Feeder Service - Way To Populate Challenge-Listing Index v1.0
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
@DatasourceName("oltp")
public interface ChallengeListingFeederDAO {
    /**
     * Get challenges for listing
     *
     * @param queryParameter the queryParameter to use
     * @return the List<ChallengeListingData> result
     */
    @SqlQueryFile("sql/challenge-feeder/get_challenges_listing.sql")
    List<ChallengeListingData> getChallenges(@ApiQueryInput QueryParameter queryParameter);
    
    /**
     * Get events for challenge listing 
     *
     * @param queryParameter the queryParameter to use
     * @return the List<EventData> result
     */
    @SqlQueryFile("sql/challenge-feeder/get_events_listing.sql")
    List<EventData> getEventsListing(@ApiQueryInput QueryParameter queryParameter);
    
    /**
     * Get checkpoints submissions count
     *
     * @param queryParameter the queryParameter to use
     * @return the List<Map<String, Object>> result
     */
    @SqlQueryFile("sql/challenge-feeder/get_checkpoints_submissions.sql")
    List<Map<String, Object>> getCheckpointsSubmissions(@ApiQueryInput QueryParameter queryParameter);
    
    /**
     * Get winners for challenge listing
     *
     * @param queryParameter the queryParameter to use
     * @return the List<WinnerData> result
     */
    @SqlQueryFile("sql/challenge-feeder/get_winners_for_challenge_listing.sql")
    List<WinnerData> getWinnersForChallengeListing(@ApiQueryInput QueryParameter queryParameter);
    
    /**
     * Get user Id
     *
     * @param queryParameter the queryParameter to use
     * @return the List<UserIdData> result
     */
    @SqlQueryFile("sql/challenge-feeder/get_challenge-userids.sql")
    List<UserIdData> getChallengeUserIds(@ApiQueryInput QueryParameter queryParameter);
}
