package com.mentoree.config.security.util;

public interface SecurityConstant {

    public static final String ACCESS_TOKEN = "AccessToken";
    public static final String REFRESH_TOKEN = "RefreshToken";
    public static final Long ACCESS_VALIDATION_TIME = 60 * 30 * 1000L; // 30 min
    public static final Long REFRESH_VALIDATION_TIME = 60 * 60 * 24 * 14 * 1000L; // 14 days
    public static final int ACCESS_COOKIE_VALID = 1800; // 30 min;

}
