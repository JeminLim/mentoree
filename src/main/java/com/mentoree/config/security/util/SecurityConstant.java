package com.mentoree.config.security.util;

public interface SecurityConstant {

    public static final Long ACCESS_VALIDATION_TIME = 60 * 30 * 1000L; // 30 min
    public static final Integer REFRESH_VALID_TIME = 60 * 60 * 24 * 15;
    public static final String ACCESS_TOKEN_COOKIE = "atc";
    public static final String UUID_COOKIE = "uc";
}
