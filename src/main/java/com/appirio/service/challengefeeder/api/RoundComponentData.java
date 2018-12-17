/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the RoundComponentData model 
 * 
 * It's added in TC Elasticsearch feeder - Add Job For populating rounds index v1.0
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
public class RoundComponentData {
    /**
     * The roundId field 
     */
    @Getter
    @Setter
    private Long roundId;

    /**
     * The componentId field 
     */
    @Getter
    @Setter
    private Long componentId;
    
    /**
     * The division field 
     */
    @Getter
    @Setter
    private String division;
    
    /**
     * The difficulty field 
     */
    @Getter
    @Setter
    private String difficulty;
    
    /**
     * The prolemId field 
     */
    @Getter
    @Setter
    private Long problemId;
    
    /**
     * The points field 
     */
    @Getter
    @Setter
    private Double points;
}
