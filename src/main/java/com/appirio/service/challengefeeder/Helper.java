/*
 * Copyright (C) 2017 Topcoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder;

import com.appirio.supply.SupplyException;
import com.appirio.tech.core.auth.AuthUser;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletResponse;

/**
 * Helper class
 *
 * @author TCSCODER
 * @version 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Helper {
    /**
     * Check the id.
     * @param id the id
     * @throws IllegalArgumentException if id is not positive
     */
    public static void checkId(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("id should be positive");
        }
    }

    /**
     * Check if the logged in user has admin role.
     * 
     * @param user the user to check
     * @throws SupplyException if the user is not admin
     */
    public static void checkAdmin(AuthUser user) throws SupplyException {
        if (!user.hasRole("administrator")) {
            throw new SupplyException("You should be admin to perform this operation", HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
