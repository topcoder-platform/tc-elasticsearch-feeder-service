/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.dao;

import com.appirio.service.challengefeeder.api.detail.ChallengeDetailData;
import com.appirio.service.challengefeeder.api.detail.RegistrantData;
import com.appirio.service.challengefeeder.api.detail.SubmissionData;
import com.appirio.supply.dataaccess.ApiQueryInput;
import com.appirio.supply.dataaccess.DatasourceName;
import com.appirio.supply.dataaccess.SqlQueryFile;
import com.appirio.tech.core.api.v3.request.QueryParameter;

import java.util.List;

/**
 * DAO to interact with challenge data
 *
 * It's added in Topcoder ElasticSearch Feeder Service - Way To Populate Challenge-Detail Index For Legacy Marathon Matches v1.0
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
}
