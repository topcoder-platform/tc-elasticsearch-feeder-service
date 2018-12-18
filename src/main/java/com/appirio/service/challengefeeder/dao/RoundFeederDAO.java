/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.dao;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.skife.jdbi.v2.sqlobject.Bind;

import com.appirio.service.challengefeeder.api.RoundComponentData;
import com.appirio.service.challengefeeder.api.RoundData;
import com.appirio.supply.dataaccess.ApiQueryInput;
import com.appirio.supply.dataaccess.DatasourceName;
import com.appirio.supply.dataaccess.SqlQueryFile;
import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.request.QueryParameter;

/**
 * DAO used to interact with database for round data.
 * 
 * It's added in TC Elasticsearch feeder - Add Job For populating rounds index v1.0
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
@DatasourceName("oltp")
public interface RoundFeederDAO {

    /**
     * Get rounds
     *
     * @param queryParameter the queryParameter to use
     * @return the List<RoundData> result
     */
    @SqlQueryFile("sql/rounds-feeder/get_rounds.sql")
    List<RoundData> getRounds(@ApiQueryInput QueryParameter queryParameter);

    /**
     * Get round components by round id
     *
     * @param queryParameter the queryParameter to use
     * @return the List<RoundComponentData> result
     */
    @SqlQueryFile("sql/rounds-feeder/get_round_components.sql")
    List<RoundComponentData> getRoundComponents(@ApiQueryInput QueryParameter queryParameter);

    /**
     * Get user ids of rounds
     *
     * @param queryParameter the queryParameter to use
     * @return the result
     */
    @SqlQueryFile("sql/rounds-feeder/get_user_ids.sql")
    List<Map<String, Object>> getUserIds(@ApiQueryInput QueryParameter queryParameter);

    /**
     * Get rounds whose registration phase started after the specified date and
     * after the last run timestamp.
     * 
     * @param date The initial date param.
     * @param lastRunTimestamp The last run timestamp.
     * @return The list of TCID.
     */
    @SqlQueryFile("sql/rounds-feeder/job/get_round_registration_phase_started.sql")
    List<TCID> getMatchesWithRegistrationPhaseStartedIds(@Bind("initialDate") Date date, @Bind("lastRunTimestamp") Long lastRunTimestamp);
}
