/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.text.*;
import java.util.*;
import java.util.Date;

/**
 * Represents the ResourceData model 
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
public class ResourceData {
    /**
     * The challengeId field
     */
    @Getter
    @Setter
    @JsonIgnore
    private Long challengeId;
    
    /**
     * The resourceId field
     */
    @Getter
    @Setter
    private Long resourceId;

    /**
     * The role field
     */
    @Getter
    @Setter
    private String role;

    /**
     * The projectPhaseId field
     */
    @Getter
    @Setter
    private Long projectPhaseId;

    /**
     * The reliability field
     */
    @Getter
    @Setter
    private String reliability;

    /**
     * The registrationDate field
     *
     */
    @Getter
    private Object registrationDate;

    /**
     * The rating field
     */
    @Getter
    @Setter
    private String rating;

    /**
     * The handle field
     */
    @Getter
    @Setter
    private String handle;

    /**
     * The userId field
     */
    @Getter
    @Setter
    private Long userId;

    @Getter
    @Setter
    private transient Long submissionCount;

    /**
     * Setter for {@link #registrationDate}
     * @param dateObject
     */
    public void setRegistrationDate(Object dateObject) {
        if (dateObject instanceof String) {
            DateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy h:mm a", Locale.US);
            try {
                this.registrationDate = dateFormat.parse((String) dateObject);
            } catch (ParseException e) {
                //nothing to do
                e.printStackTrace();
            }
        } else if (dateObject instanceof Date) {
            this.registrationDate = dateObject;
        }
    }
}
