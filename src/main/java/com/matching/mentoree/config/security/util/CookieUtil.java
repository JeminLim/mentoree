package com.matching.mentoree.config.security.util;

import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Optional;

import static com.matching.mentoree.config.security.util.SecurityConstant.ACCESS_COOKIE_VALID;
import static com.matching.mentoree.config.security.util.SecurityConstant.ACCESS_TOKEN;

@Component
public class CookieUtil {

    public void addCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(ACCESS_TOKEN, token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(ACCESS_COOKIE_VALID);
        response.addCookie(cookie);
    }

    public  Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if(cookies != null && cookies.length > 0) {
            for(Cookie cookie : cookies)
                if(cookie.getName().equals(name))
                    return Optional.of(cookie);

        }
        return null;
    }

    public void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if(cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals(name)){
                    cookie.setValue(null);
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }

}
