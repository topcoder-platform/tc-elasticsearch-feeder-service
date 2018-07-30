/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.dao;

import com.appirio.service.challengefeeder.api.detail.MmResult;
import com.appirio.supply.dataaccess.ApiQueryInput;
import com.appirio.supply.dataaccess.DatasourceName;
import com.appirio.supply.dataaccess.SqlQueryFile;
import com.appirio.tech.core.api.v3.request.QueryParameter;

import java.util.List;

/**
 * DAO to interact with marathon match on topcoder_dw
 *
 */
@DatasourceName("topcoder_dw")
public interface MarathonMatchResultFeederDAO {

    /**
     * Get marathon match result
     *
     * @param queryParameter the queryParameter to use
     * @return list of MmResult
     */
    @SqlQueryFile("sql/mm-feeder-into-challenges/get_marathon_match_result.sql")
    List<MmResult> getMmFinalResult(@ApiQueryInput QueryParameter queryParameter);
}
