package com.matching.mentoree.util;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

public class CommonUtil {

    /**
     * Return login user email depends on method that user logged in
     * @return email
     */
    public static String getLoginEmail() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = "";
        if(auth instanceof DefaultOAuth2User) {
            email = (String) ((DefaultOAuth2User) auth).getAttributes().get("email");
        }
        else if(auth instanceof UsernamePasswordAuthenticationToken) {
            email = (String) auth.getPrincipal();
        }
        return email;
    }

}
