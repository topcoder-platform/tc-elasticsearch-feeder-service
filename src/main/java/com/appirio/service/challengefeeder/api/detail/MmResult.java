/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api.detail;

import lombok.Getter;
import lombok.Setter;

public class MmResult {
    /**
     * userId field
     */
    @Getter
    @Setter
    private Long userId;

    /**
     * challengeId field
     */
    @Getter
    @Setter
    private Long challengeId;

    /**
     * provisionalScore field
     */
    @Getter
    @Setter
    private Double provisionalScore;

    /**
     * finalScore field
     */
    @Getter
    @Setter
    private Double finalScore;

    /**
     * finalRakn field
     */
    @Getter
    @Setter
    private Integer finalRank;

    /**
     * provisionalRank field
     */
    @Getter
    @Setter
    private Integer provisionalRank;
}
