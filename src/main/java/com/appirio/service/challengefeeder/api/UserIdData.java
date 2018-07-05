package com.appirio.service.challengefeeder.api;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

public class UserIdData {

	/**
     * The challengeId field
     */
    @Getter
    @Setter
    @JsonIgnore
    private Long challengeId;
    
    /**
     * The user Id field
     */
    @Getter
    @Setter
    private Long userId;

}
