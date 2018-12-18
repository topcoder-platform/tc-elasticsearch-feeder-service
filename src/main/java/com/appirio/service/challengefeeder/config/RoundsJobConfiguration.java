package com.appirio.service.challengefeeder.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the RoundsJobConfiguration model for round job configuration
 * 
 * It's added in TC Elasticsearch feeder - Add Job For populating rounds index v1.0
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
public class RoundsJobConfiguration extends JobConfiguration {
    /**
     * The days to subtract for rounds.
     */
    @JsonProperty
    @Getter
    @Setter
    private int roundsDaysToSubtract;
}
