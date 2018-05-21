/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.manager;

import com.appirio.service.challengefeeder.api.DataScienceData;
import com.appirio.service.challengefeeder.api.IdentifiableData;
import com.appirio.service.challengefeeder.api.MarathonMatchData;
import com.appirio.service.challengefeeder.api.SRMData;
import com.appirio.service.challengefeeder.dto.DataScienceFeederParam;
import com.appirio.service.challengefeeder.dto.MmFeederParam;
import com.appirio.supply.SupplyException;
import com.appirio.tech.core.api.v3.request.FieldSelector;
import com.appirio.tech.core.api.v3.request.FilterParameter;
import com.appirio.tech.core.api.v3.request.QueryParameter;

import io.searchbox.client.JestClient;
import io.searchbox.core.Bulk;
import io.searchbox.core.Bulk.Builder;
import io.searchbox.core.Delete;
import io.searchbox.core.Index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;


/**
 * Helper class which provides common utility methods for the Data Science (Marathon Match and SRM) feeder manager.
 * <p>
 * Added in Topcoder - Add Endpoints To Populating Marathon Matches And SRMs Into Elasticsearch v1.0
 * </p>
 * 
 * <p>
 * Version 1.1 - Topcoder ElasticSearch Feeder Service - Way To Populate Challenge-Listing Index For Legacy Marathon Matches v1.0
 * - add common methods to validate the marathon match data
 * </p>
 *
 * @author TCCoder
 * @version 1.1 
 */
final class DataScienceHelper {
    /**
     * Check missed ids
     *
     * @param param the param to use
     * @param result the result to use
     * @throws SupplyException if any error occurs
     */
    static void checkMissedIds(MmFeederParam param, List<? extends IdentifiableData> result) throws SupplyException {
        List<Long> idsNotFound = new ArrayList<Long>();
        for (Long id : param.getRoundIds()) {
            boolean hit = false;
            for (IdentifiableData data : result) {
                if (id.longValue() == data.getId().longValue()) {
                    hit = true;
                    break;
                }
            }
            if (!hit) {
                idsNotFound.add(id);
            }
        }
        if (!idsNotFound.isEmpty()) {
            throw new SupplyException("The round ids not found: " + idsNotFound, HttpServletResponse.SC_NOT_FOUND);
        }

    }

    /**
     * Check marathon feeder parameter
     *
     * @param param the param to use
     * @param defaultType the defaultType to use
     * @throws SupplyException if any error occurs
     */
    static void checkMarathonFeederParam(MmFeederParam param, String defaultType) throws SupplyException {
        if (param.getType() == null || param.getType().trim().length() == 0) {
            param.setType(defaultType);
        }
        if (param.getIndex() == null || param.getIndex().trim().length() == 0) {
            throw new SupplyException("The index should be non-null and non-empty string.", HttpServletResponse.SC_BAD_REQUEST);
        }

        if (param.getRoundIds() == null || param.getRoundIds().size() == 0) {
            throw new SupplyException("Round ids must be provided", HttpServletResponse.SC_BAD_REQUEST);
        }
        if (param.getRoundIds().contains(null)) {
            throw new SupplyException("Null round id is not allowed", HttpServletResponse.SC_BAD_REQUEST);
        }
        
        Set<Long> duplicateIds = new HashSet<Long>();
        for (Long id : param.getRoundIds()) {
            if (id.longValue() <= 0) {
                throw new SupplyException("Round id should be positive", HttpServletResponse.SC_BAD_REQUEST);
            }
            if (param.getRoundIds().indexOf(id) != param.getRoundIds().lastIndexOf(id)) {
                duplicateIds.add(id);
            }
        }
        
        if (!duplicateIds.isEmpty()) {
            throw new SupplyException("The round ids are duplicate:" + duplicateIds, HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    
    /**
     * Checks if given param is valid: non-null/non-empty index, non-null/non-empty roundIds, and roundIds does not
     * contain null element.
     *
     * @param param
     *            the data science feeder param to check
     * @param defaultType
     *            the default type to use if given param's type is null or empty
     * @throws SupplyException
     *             if given param is invalid.
     */
    static void checkDataScienceFeederParam(DataScienceFeederParam param, String defaultType) throws SupplyException {
        if (param.getType() == null || param.getType().trim().length() == 0) {
            param.setType(defaultType);
        }
        if (param.getIndex() == null || param.getIndex().trim().length() == 0) {
            throw new SupplyException("The index should be non-null and non-empty string.",
                HttpServletResponse.SC_BAD_REQUEST);
        }
        if (param.getRoundIds() == null || param.getRoundIds().size() == 0) {
            throw new SupplyException("Round ids must be provided", HttpServletResponse.SC_BAD_REQUEST);
        }
        if (param.getRoundIds().contains(null)) {
            throw new SupplyException("Null round id is not allowed", HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Build a query parameter with IN operator to filter only matching data in given list
     *
     * @param list
     *            the list of values to be matched with
     * @param column
     *            the name of field to filter
     * @return query parameter with IN filter
     */
    static QueryParameter buildInQueryParameter(List<?> list, String column) {
        StringBuilder filterQuery = new StringBuilder(column + "=in(");
        for (int i = 0; i < list.size(); ++i) {
            filterQuery.append(list.get(i));
            if (i < list.size() - 1) {
                filterQuery.append(", ");
            }
        }
        filterQuery.append(")");

        // build search query
        FilterParameter filter = new FilterParameter(filterQuery.toString());
        QueryParameter queryParameter = new QueryParameter(new FieldSelector());
        queryParameter.setFilter(filter);
        return queryParameter;
    }

    /**
     * Check if all MM or SRM data with given roundIds exist in persistence.
     *
     * @param roundIds
     *            the roundIds of MM or SRM to check
     * @param data
     *            the existing data from persistence
     * @throws SupplyException
     *             if any MM or SRM does not exist in persistence
     */
    static <T extends DataScienceData> void checkDataScienceExist(List<Long> roundIds, List<T> data)
        throws SupplyException {
        List<Long> idsNotFound = new ArrayList<Long>();
        for (Long roundId : roundIds) {
            boolean hit = false;
            for (DataScienceData dataScience : data) {
                if (roundId.longValue() == dataScience.getRoundId().longValue()) {
                    hit = true;
                    break;
                }
            }
            if (!hit) {
                idsNotFound.add(roundId);
            }
        }
        if (!idsNotFound.isEmpty()) {
            throw new SupplyException("The round ids not found: " + idsNotFound, HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Associate users to data science (SRM or MM) contests
     *
     * @param data
     *            the data science track data (MM or SRM) to associate to
     * @param users
     *            the users data
     */
    static <T extends DataScienceData> void associateDataScienceUsers(List<T> data, List<Map<String, Object>> users) {
        for (DataScienceData dataScience : data) {
            long numberOfRegistrants = 0;
            for (Map<String, Object> user : users) {
                if (user.get("id").toString().equals(dataScience.getId().toString())) {
                    // get userIds
                    List<Long> userIds = dataScience.getUserIds();
                    if (userIds == null) {
                        // create new empty list
                        userIds = new ArrayList<Long>();
                        dataScience.setUserIds(userIds);
                    }

                    List<String> isRateds = null;
                    String isRated = null;
                    // get isRatedForMM or isRatedForSRM
                    if (dataScience instanceof MarathonMatchData) {
                        isRateds = ((MarathonMatchData) dataScience).getIsRatedForMM();
                        if (isRateds == null) {
                            isRateds = new ArrayList<String>();
                            ((MarathonMatchData) dataScience).setIsRatedForMM(isRateds);
                        }
                        isRated = (String) user.get("isRatedForMM");
                    } else if (dataScience instanceof SRMData) {
                        isRateds = ((SRMData) dataScience).getIsRatedForSRM();
                        if (isRateds == null) {
                            isRateds = new ArrayList<String>();
                            ((SRMData) dataScience).setIsRatedForSRM(isRateds);
                        }
                        isRated = (String) user.get("isRatedForSRM");
                    }

                    // add to the data
                    if (user.get("userId") != null) {
                        userIds.add(Long.parseLong(user.get("userId").toString()));
                        isRateds.add(isRated);
                        numberOfRegistrants++;
                    }
                }
            }
            dataScience.setNumberOfRegistrants(numberOfRegistrants);
        }
    }

    /**
     * Push data sciences (MM or SRM) to ElasticSearch
     *
     * @param jestClient
     *            The ElasticSearch Jest client
     * @param param
     *            the feeder param
     * @param dataSciences
     *            the data to push
     * @throws SupplyException
     *             if any error occurs when pushing the data
     */
    static <T extends DataScienceData> void pushDataScience(JestClient jestClient, DataScienceFeederParam param,
        List<T> dataSciences) throws SupplyException {
        // first delete the index and then create it
        Builder builder = new Bulk.Builder();
        for (DataScienceData data : dataSciences) {
            builder.addAction(new Delete.Builder(data.getRoundId().toString()).index(param.getIndex()).type(param
                .getType()).build());
            builder.addAction(new Index.Builder(data).index(param.getIndex()).type(param.getType()).id(data.getRoundId()
                .toString()).build());
        }
        Bulk bulk = builder.build();
        try {
            jestClient.execute(bulk);
        } catch (IOException ioe) {
            SupplyException se = new SupplyException("Internal server error occurs", ioe);
            se.setStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw se;
        }
    }
}
