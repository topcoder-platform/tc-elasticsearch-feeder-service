/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.dao;

import java.sql.Date;
import java.util.*;

import com.appirio.service.challengefeeder.api.ChallengeData;
import com.appirio.service.challengefeeder.api.EventData;
import com.appirio.service.challengefeeder.api.PhaseData;
import com.appirio.service.challengefeeder.api.PrizeData;
import com.appirio.service.challengefeeder.api.ResourceData;
import com.appirio.service.challengefeeder.api.SubmissionData;
import com.appirio.service.challengefeeder.api.TermsOfUseData;
import com.appirio.service.challengefeeder.dto.*;
import com.appirio.supply.dataaccess.ApiQueryInput;
import com.appirio.supply.dataaccess.DatasourceName;
import com.appirio.supply.dataaccess.SqlQueryFile;
import com.appirio.tech.core.api.v3.*;
import com.appirio.tech.core.api.v3.request.QueryParameter;
import org.skife.jdbi.v2.sqlobject.*;

/**
 * DAO to interact with marathon match data
 *
 * It's added in Topcoder - Populate Marathon Match Related Data Into Challenge Model In Elasticsearch v1.0
 * 
 * @author TCCODER
 * @version 1.0
 */
@DatasourceName("oltp")
public interface MmFeederDAO {
    /**
     * Get marathon matches
     *
     * @param queryParameter the queryParameter to use
     * @return the List<ChallengeData> result
     */
    @SqlQueryFile("sql/mm-feeder-into-challenges/get_marathon_matches.sql")
    List<ChallengeData> getMarathonMatches(@ApiQueryInput QueryParameter queryParameter);
    
    /**
     * Get submissions
     *
     * @param queryParameter the queryParameter to use
     * @return the List<SubmissionData> result
     */
    @SqlQueryFile("sql/mm-feeder-into-challenges/get_submissions.sql")
    List<SubmissionData> getSubmissions(@ApiQueryInput QueryParameter queryParameter);
    
    /**
     * Get terms
     *
     * @param queryParameter the queryParameter to use
     * @return the List<TermsOfUseData> result
     */
    @SqlQueryFile("sql/mm-feeder-into-challenges/get-terms.sql")
    List<TermsOfUseData> getTerms(@ApiQueryInput QueryParameter queryParameter);
    
    /**
     * Get events
     *
     * @param queryParameter the queryParameter to use
     * @return the List<EventData> result
     */
    @SqlQueryFile("sql/mm-feeder-into-challenges/get_events.sql")
    List<EventData> getEvents(@ApiQueryInput QueryParameter queryParameter);
    
    /**
     * Get resources
     *
     * @param queryParameter the queryParameter to use
     * @return the List<ResourceData> result
     */
    @SqlQueryFile("sql/mm-feeder-into-challenges/get_resources.sql")
    List<ResourceData> getResources(@ApiQueryInput QueryParameter queryParameter);
    
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
