/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the FileTypeData model 
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
public class FileTypeData {
    /**
     * The challengeId field
     */
    @Getter
    @Setter
    @JsonIgnore
    private Long challengeId;
    
    /**
     * The description field
     */
    @Getter
    @Setter
    private String description;

    /**
     * The fileTypeId field
     */
    @Getter
    @Setter
    private Long fileTypeId;


}
