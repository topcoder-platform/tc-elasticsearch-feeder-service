/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.skife.jdbi.v2.sqlobject.Bind;

import com.appirio.service.challengefeeder.api.FileTypeData;
import com.appirio.service.challengefeeder.api.PhaseData;
import com.appirio.service.challengefeeder.api.PrizeData;
import com.appirio.service.challengefeeder.api.UserIdData;
import com.appirio.service.challengefeeder.api.challengelisting.ChallengeListingData;
import com.appirio.service.challengefeeder.api.challengelisting.EventData;
import com.appirio.service.challengefeeder.api.challengelisting.WinnerData;
import com.appirio.service.challengefeeder.dto.DatabaseTimestamp;
import com.appirio.supply.dataaccess.ApiQueryInput;
import com.appirio.supply.dataaccess.DatasourceName;
import com.appirio.supply.dataaccess.SqlQueryFile;
import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.request.QueryParameter;

/**
 * DAO to interact with challenge data
 * 
 * It's added in Topcoder ElasticSearch Feeder Service - Way To Populate Challenge-Listing Index v1.0
 * 
 * Version 1.1 - Topcoder Elasticsearch Feeder Service - Jobs Cleanup And Improvement v1.0
 * - add more methods for the listing data
 * 
 * 
 * @author TCCoder
 * @version 1.1 
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
    
    /**
     * Get challenge technologies
     *
     * @param queryParameter the queryParameter to use
     * @return the List<Map<String,Object>> result
     */
    @SqlQueryFile("sql/challenge-feeder/get_challenge_technologies.sql")
    List<Map<String, Object>> getChallengeTechnologies(@ApiQueryInput QueryParameter queryParameter);
    
    /**
     * Get challenge platforms
     *
     * @param queryParameter the queryParameter to use
     * @return the List<Map<String,Object>> result
     */
    @SqlQueryFile("sql/challenge-feeder/get_challenge_plagforms.sql")
    List<Map<String, Object>> getChallengePlatforms(@ApiQueryInput QueryParameter queryParameter);
    
    /**
     * Get prize of type prize points
     *
     * @param challengeId challengeId
     * @return the list of prizes
     */
    @SqlQueryFile("sql/challenge-feeder/get_challenge_pointsPrize.sql")
    List<PrizeData> getPointsPrize(@ApiQueryInput QueryParameter queryParameter);
    
    /**
     * Get group ids 
     *
     * @param queryParameter the queryParameter to use
     * @return the result
     */
    @SqlQueryFile("sql/challenge-feeder/get_group_ids.sql")
    List<Map<String, Object>> getGroupIds(@ApiQueryInput QueryParameter queryParameter);
    
    /**
     * Get phases
     *
     * @param queryParameter the queryParameter to use
     * @return the List<PhaseData> result
     */
    @SqlQueryFile("sql/challenge-feeder/get_phases.sql")
    List<PhaseData> getPhases(@ApiQueryInput QueryParameter queryParameter);
    
    /**
     * Get prizes 
     *
     * @param queryParameter the queryParameter to use
     * @return the result
     */
    @SqlQueryFile("sql/challenge-feeder/get_prizes.sql")
    List<PrizeData> getPrizes(@ApiQueryInput QueryParameter queryParameter);
    
    /**
     * Get file types 
     *
     * @param queryParameter the queryParameter to use
     * @return the result
     */
    @SqlQueryFile("sql/challenge-feeder/get_file_types.sql")
    List<FileTypeData> getFileTypes(@ApiQueryInput QueryParameter queryParameter);
    
    /**
     * Get changed challenge ids
     *
     * @param lastRunTimestamp the lastRunTimestamp to use
     * @return the List<TCID> result
     */
    @SqlQueryFile("sql/challenge-feeder/job/get_changed_challenge_ids.sql")
    List<TCID> getChangedChallengeIds(@Bind("lastRunTimestamp") Date lastRunTimestamp);

    /**
     * Get timestamp 
     *
     * @param queryParameter the queryParameter to use
     * @return the result
     */
    @SqlQueryFile("sql/challenge-feeder/job/get_timestamp.sql")
    DatabaseTimestamp getTimestamp();
}
