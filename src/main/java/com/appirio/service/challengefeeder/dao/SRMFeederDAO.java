/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.dao;

import com.appirio.service.challengefeeder.api.SRMData;
import com.appirio.supply.dataaccess.ApiQueryInput;
import com.appirio.supply.dataaccess.DatasourceName;
import com.appirio.supply.dataaccess.SqlQueryFile;
import com.appirio.tech.core.api.v3.request.QueryParameter;

import java.util.List;
import java.util.Map;


/**
 * DAO to interact with SRMs data
 * <p>
 * Added in Topcoder - Add Endpoints To Populating Marathon Matches And SRMs Into Elasticsearch v1.0
 * </p>
 *
 * @author TCCODER
 * @version 1.0
 */
@DatasourceName("oltp")
public interface SRMFeederDAO {
    /**
     * Get SRMs
     *
     * @param queryParameter
     *            the queryParameter to use
     * @return the List<MarathonMatchData> result
     */
    @SqlQueryFile("sql/srms-feeder/get_srms.sql")
    List<SRMData> getSRMs(@ApiQueryInput QueryParameter queryParameter);

    /**
     * Get user ids of SRMs
     *
     * @param queryParameter
     *            the queryParameter to use
     * @return the result
     */
    @SqlQueryFile("sql/srms-feeder/get_user_ids.sql")
    List<Map<String, Object>> getUserIds(@ApiQueryInput QueryParameter queryParameter);
}
