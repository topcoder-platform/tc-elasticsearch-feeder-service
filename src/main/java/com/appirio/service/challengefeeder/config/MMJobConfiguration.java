package com.appirio.service.challengefeeder.config;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the MMJobConfiguration
 * 
 * It's added in Topcoder Elasticsearch Feeder Service - Jobs Cleanup And Improvement v1.0
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
public class MMJobConfiguration extends JobConfiguration {
    /**
     * The days to subtract in MarathonMatchesJob.
     */
    @JsonProperty
    @Getter
    @Setter
    private int marathonMatchesDaysToSubtract;
    
    /**
     * Represents the marathonMatchesForumUrl.
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String marathonMatchesForumUrl;
}
