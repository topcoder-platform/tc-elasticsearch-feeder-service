/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api.detail;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

public class Rank {
    /**
     * final rank
     */
    @Getter
    @Setter
    @SerializedName("final")
    private Integer finalRank;

    /**
     * provisional rank
     */
    @Getter
    @Setter
    private Integer interim;
}
