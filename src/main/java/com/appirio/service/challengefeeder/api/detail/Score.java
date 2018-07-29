/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.api.detail;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

public class Score {
    /**
     * final score
     */
    @Getter
    @Setter
    @SerializedName("final")
    private Double finalScore;

    /**
     * provisional score
     */
    @Getter
    @Setter
    private Double provisional;
}
