/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.manager;

import java.util.List;

/**
 * ChallengeFeederUtil provides common methods such as associating the challenge data.
 * 
 * It's added in Topcoder - Populate Marathon Match Related Data Into Challenge Model In Elasticsearch v1.0
 *
 * Version 1.1 - Topcoder - Elasticsearch Service - Populate Challenge Points Prize In Challenges Index
 * 	- add the methods to associate the prize points with the challenge ids
 *
 * Version 1.2 - Topcoder ElasticSearch Feeder Service - Way To Populate Challenge-Detail Index
 *  - added {@link #getColorStyle(Integer)}
 *  
 * Version 1.3 - Topcoder Elasticsearch Feeder Service - Jobs Cleanup And Improvement v1.0
 * - remove the useless methods
 * 
 *
 * @author TCSCODER
 * @version 1.3 
 */
public class ChallengeFeederUtil {
    /**
     * Get color style
     *
     * @param rating the rating to use
     * @return the String result representing the color
     * @since 1.2
     */
    static String getColorStyle(Integer rating) {

        if (rating == null) {
            return "color: #000000";
        }

        if (rating < 0) {
            return "color: #FF9900"; // orange
        }
        if (rating < 900) {
            return "color: #999999";// gray
        }
        if (rating < 1200) {
            return "color: #00A900";// green
        }
        if (rating < 1500) {
            return "color: #6666FF";// blue
        }
        if (rating < 2200) {
            return "color: #DDCC00";// yellow
        }
        if (rating > 2199) {
            return "color: #EE0000";// red
        }
        // return black otherwise.
        return "color: #000000";

    }
    
    /**
     * List the items as string
     *
     * @param items the items to use
     * @return the String result, separated by comma
     */
    static String listAsString(List<? extends Object> items) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < items.size(); ++i) {
            res.append(items.get(i).toString());
            if (i < items.size() - 1) {
                res.append(", ");
            }
        }

        return res.toString();
    }
}
