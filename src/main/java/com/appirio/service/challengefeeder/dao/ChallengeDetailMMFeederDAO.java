/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.dao;

import java.sql.Date;
import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;

import com.appirio.service.challengefeeder.api.challengedetail.ChallengeDetailData;
import com.appirio.service.challengefeeder.api.challengedetail.RegistrantData;
import com.appirio.service.challengefeeder.api.challengedetail.SubmissionData;
import com.appirio.service.challengefeeder.dto.DatabaseTimestamp;
import com.appirio.supply.dataaccess.ApiQueryInput;
import com.appirio.supply.dataaccess.DatasourceName;
import com.appirio.supply.dataaccess.SqlQueryFile;
import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.request.QueryParameter;

/**
 * DAO to interact with challenge data
 *
 * It's added in Topcoder ElasticSearch Feeder Service - Way To Populate Challenge-Detail Index For Legacy Marathon Matches v1.0
 * 
 * Version 1.1 - Topcoder Elasticsearch Feeder Service - Jobs Cleanup And Improvement v1.0
 * - add more methods for marathon match
 * 
 * @author TCCODER
 * @version 1.1 
 */
@DatasourceName("oltp")
public interface ChallengeDetailMMFeederDAO {

    /**
     * Get marathon matches for challenge details 
     *
     * @param queryParameter the queryParameter to use
     * @return the result
     */
    @SqlQueryFile("sql/mm-feeder-into-challenges/get_marathon_matches_for_challenge_details.sql")
    List<ChallengeDetailData> getMarathonMatchesForChallengeDetails(@ApiQueryInput QueryParameter queryParameter);
    
    /**
     * Get registrants
     *
     * @param queryParameter the queryParameter to use
     * @return the List<RegistrantData> result
     */
    @SqlQueryFile("sql/mm-feeder-into-challenges/get_registrants.sql")
    List<RegistrantData> getRegistrants(@ApiQueryInput QueryParameter queryParameter);
    
    /**
     * Get submissions for challenge detail
     *
     * @param queryParameter the queryParameter to use
     * @return the List<SubmissionData> result
     */
    @SqlQueryFile("sql/mm-feeder-into-challenges/get_submissions_for_challenge_detail.sql")
    List<SubmissionData> getSubmissionsForChallengeDetail(@ApiQueryInput QueryParameter queryParameter);
    
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
}
