/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the RoundData model 
 * 
 * It's added in TC Elasticsearch feeder - Add Job For populating rounds index v1.0
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
public class RoundData extends DataScienceData {
    /**
     * The isRated field 
     */
    @Getter
    @Setter
    private List<String> isRated;

    /**
     * The contestId field 
     */
    @Getter
    @Setter
    private Long contestId;
    
    /**
     * The roundTypeId field 
     */
    @Getter
    @Setter
    private Integer roundTypeId;
    
    /**
     * The roundTypeName field 
     */
    @Getter
    @Setter
    private String roundTypeName;
    
    /**
     * The components field 
     */
    @Getter
    @Setter
    private List<RoundComponentData> components;
}
