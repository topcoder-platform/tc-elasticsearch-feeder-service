package com.appirio.service.challengefeeder.api.challengedetail;

import java.util.Date;

import com.appirio.service.challengefeeder.helper.CustomDateDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the CheckpointData model 
 * 
 * It's added in Topcoder ElasticSearch Feeder Service - Way To Populate Challenge-Detail Index For Legacy Marathon Matches v1.0
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
public class CheckpointSubmissionData {
    /**
     * The submissionId field.
     */
    @Getter
    @Setter
    private Long submissionId;

    /**
     * The submitter field.
     */
    @Getter
    @Setter
    private String submitter;

    /**
     * The submissionTime field.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @Getter
    @Setter
    private Date submissionTime;  
    
    /**
     * The challengeId field.
     */
    @Getter
    @Setter
    private Long challengeId;
}
