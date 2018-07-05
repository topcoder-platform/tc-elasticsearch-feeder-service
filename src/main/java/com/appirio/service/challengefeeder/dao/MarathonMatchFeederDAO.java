/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.dao;

import com.appirio.service.challengefeeder.api.MarathonMatchData;
import com.appirio.supply.dataaccess.ApiQueryInput;
import com.appirio.supply.dataaccess.DatasourceName;
import com.appirio.supply.dataaccess.SqlQueryFile;
import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.request.QueryParameter;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.skife.jdbi.v2.sqlobject.Bind;


/**
 * DAO to interact with marathon match data
 * <p>
 * Added in Topcoder - Add Endpoints To Populating Marathon Matches And SRMs Into Elasticsearch v1.0
 * </p>
 *
 * <p>
 * Version 1.1 - Create CronJob For Populating Marathon Matches and SRMs To Elasticsearch v1.0
 * - Added getTimestamp method to get the current timestamp from the database
 * </p>
 * @author TCCODER
 * @version 1.1
 */
@DatasourceName("oltp")
public interface MarathonMatchFeederDAO {
    /**
     * Get marathon matches
     *
     * @param queryParameter
     *            the queryParameter to use
     * @return the List<MarathonMatchData> result
     */
    @SqlQueryFile("sql/mmatches-feeder/get_mmatches.sql")
    List<MarathonMatchData> getMarathonMatches(@ApiQueryInput QueryParameter queryParameter);

    /**
     * Get user ids of marathon matches
     *
     * @param queryParameter
     *            the queryParameter to use
     * @return the result
     */
    @SqlQueryFile("sql/mmatches-feeder/get_user_ids.sql")
    List<Map<String, Object>> getUserIds(@ApiQueryInput QueryParameter queryParameter);

    /**
     * Get the marathon matches whose registration phase started after the specified date and after the last run timestamp.
     * 
     * @param date The date param.
     * @param lastRunTimestamp The last run timestamp.
     * @return The list of TCID.
     */
    @SqlQueryFile("sql/mmatches-feeder/job/get_mm_registration_phase_started.sql")
    List<TCID> getMatchesWithRegistrationPhaseStartedIds(@Bind("initialDate") Date date, @Bind("lastRunTimestamp") Long lastRunTimestamp);
}
