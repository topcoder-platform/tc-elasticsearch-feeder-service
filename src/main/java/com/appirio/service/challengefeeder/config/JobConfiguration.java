package com.appirio.service.challengefeeder.config;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the JobConfiguration
 * 
 * It's added in Topcoder Elasticsearch Feeder Service - Jobs Cleanup And Improvement v1.0
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
public class JobConfiguration {
    /**
     * The index field
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String indexName;
    
    /**
     * Represents the batch update size field.
     */
    @JsonProperty
    @Getter
    @Setter
    private int batchUpdateSize;
}
