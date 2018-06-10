/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api.challengedetail;

import java.util.Date;

import com.appirio.service.challengefeeder.api.ResourceData;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the RegistrantData model 
 * 
 * It's added in Topcoder ElasticSearch Feeder Service - Way To Populate Challenge-Detail Index For Legacy Marathon Matches v1.0
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
public class RegistrantData extends ResourceData {
    /**
     * The colorStyle field
     */
    @Getter
    @Setter
    private String colorStyle;
    
    /**
     * The submissionDate field
     */
    @Getter
    @Setter
    private Date submissionDate;
}
