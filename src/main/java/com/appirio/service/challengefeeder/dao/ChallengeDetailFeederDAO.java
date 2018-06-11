/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.dao;

import com.appirio.service.challengefeeder.api.detail.ChallengeDetailData;
import com.appirio.service.challengefeeder.api.detail.DocumentData;
import com.appirio.service.challengefeeder.api.detail.RegistrantData;
import com.appirio.service.challengefeeder.api.detail.SubmissionData;
import com.appirio.service.challengefeeder.api.detail.TermsOfUseData;
import com.appirio.supply.dataaccess.ApiQueryInput;
import com.appirio.supply.dataaccess.DatasourceName;
import com.appirio.supply.dataaccess.SqlQueryFile;
import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.request.QueryParameter;
import org.skife.jdbi.v2.sqlobject.Bind;

import java.util.Date;
import java.util.List;

/**
 * DAO to interact with challenge detail data
 *
 * @author TCCODER
 * @version 1.0
 */
@DatasourceName("oltp")
public interface ChallengeDetailFeederDAO {
    /**
     * Get challenges specs
     *
     * @param queryParameter the queryParameter to use
     * @return the List<ChallengeDetailData> result
     */
    @SqlQueryFile("sql/challenge-feeder/detail/get_specs.sql")
    List<ChallengeDetailData> getChallenges(@ApiQueryInput QueryParameter queryParameter);

    /**
     * Get registrants
     *
     * @param queryParameter the queryParameter to use
     * @return the List<RegistrantData> result
     */
    @SqlQueryFile("sql/challenge-feeder/detail/get_registrants.sql")
    List<RegistrantData> getRegistrants(@ApiQueryInput QueryParameter queryParameter);

    /**
     * Get submissions
     *
     * @param queryParameter the queryParameter to use
     * @return the result
     */
    @SqlQueryFile("sql/challenge-feeder/detail/get_submissions.sql")
    List<SubmissionData> getSubmissions(@ApiQueryInput QueryParameter queryParameter);

    /**
     * Get terms
     *
     * @param queryParameter the queryParameter to use
     * @return the result
     */
    @SqlQueryFile("sql/challenge-feeder/detail/get_terms.sql")
    List<TermsOfUseData> getTerms(@ApiQueryInput QueryParameter queryParameter);

    /**
     * Get documents
     *
     * @param queryParameter the queryParameter to use
     * @return the result
     */
    @SqlQueryFile("sql/challenge-feeder/detail/get_documents.sql")
    List<DocumentData> getDocuments(@ApiQueryInput QueryParameter queryParameter);

    /**
     * Get changed challenge ids
     *
     * @param lastRunTimestamp the lastRunTimestamp to use
     * @return the List<TCID> result
     */
    @SqlQueryFile("sql/challenge-feeder/job/get_changed_challenge_detail_ids.sql")
    List<TCID> getChangedChallengeIds(@Bind("lastRunTimestamp") Date lastRunTimestamp);
}
