/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.dao;

import com.appirio.service.challengefeeder.api.EventData;
import com.appirio.service.challengefeeder.api.PhaseData;
import com.appirio.service.challengefeeder.api.PrizeData;
import com.appirio.service.challengefeeder.api.challengelisting.ChallengeListingData;
import com.appirio.service.challengefeeder.api.challengelisting.WinnerData;
import com.appirio.service.challengefeeder.dto.DatabaseTimestamp;
import com.appirio.supply.dataaccess.ApiQueryInput;
import com.appirio.supply.dataaccess.DatasourceName;
import com.appirio.supply.dataaccess.SqlQueryFile;
import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.request.QueryParameter;

import java.sql.Date;
import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;

/**
 * DAO to interact with marathon match data
 *
 * It's added in Topcoder ElasticSearch Feeder Service - Way To Populate Challenge-Listing Index For Legacy Marathon Matches v1.0
 * 
 * Version 1.1 - Topcoder Elasticsearch Feeder Service - Jobs Cleanup And Improvement v1.0
 * - add more methods for the listing marathon data
 * 
 * 
 * @author TCCODER
 * @version 1.1 
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
     * Get timestamp
     *
     * @return the result
     */
    @SqlQueryFile("sql/challenge-feeder/job/get_timestamp.sql")
    DatabaseTimestamp getTimestamp();

    /**
     * Get the marathon matches whose registration phase started after the specified date and after the last run timestamp.
     * @param date
     * @param lastRunTimestamp
     * @return
     */
    @SqlQueryFile("sql/mmatches-feeder/job/get_mm_registration_phase_started.sql")
    List<TCID> getMatchesWithRegistrationPhaseStartedIds(@Bind("initialDate") Date date, @Bind("lastRunTimestamp") Long lastRunTimestamp);
    
    /**
     * Get phases
     *
     * @param queryParameter the queryParameter to use
     * @return the List<PhaseData> result
     */
    @SqlQueryFile("sql/mm-feeder-into-challenges/get-phases.sql")
    List<PhaseData> getPhases(@ApiQueryInput QueryParameter queryParameter);
    
    /**
     * Get prizes
     *
     * @param queryParameter the queryParameter to use
     * @return the List<PrizeData> result
     */
    @SqlQueryFile("sql/mm-feeder-into-challenges/get_prizes.sql")
    List<PrizeData> getPrizes(@ApiQueryInput QueryParameter queryParameter);
    
    /**
     * Get events
     *
     * @param queryParameter the queryParameter to use
     * @return the List<EventData> result
     */
    @SqlQueryFile("sql/mm-feeder-into-challenges/get_events.sql")
    List<EventData> getEvents(@ApiQueryInput QueryParameter queryParameter);
}
