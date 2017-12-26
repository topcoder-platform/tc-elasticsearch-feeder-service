/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;


/**
 * Represents the PrizeData model 
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
public class PrizeData {
    /**
     * The challengeId field
     */
    @Getter
    @Setter
    @JsonIgnore
    private Long challengeId;
    
    /**
     * The amount field
     */
    @Getter
    @Setter
    private Double amount;

    /**
     * The place field
     */
    @Getter
    @Setter
    private int place;

    /**
     * The prizeId field
     */
    @Getter
    @Setter
    private Long prizeId;
}
