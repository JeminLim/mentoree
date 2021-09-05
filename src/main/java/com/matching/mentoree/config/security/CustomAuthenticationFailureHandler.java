package com.matching.mentoree.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.info("login failed");
        String msg = "";
        if(exception instanceof BadCredentialsException || exception instanceof UsernameNotFoundException) {
            msg = "아이디 또는 비밀번호가 틀립니다.";
        }

        setDefaultFailureUrl("/login?error=" + URLEncoder.encode(msg, StandardCharsets.UTF_8));
        super.onAuthenticationFailure(request, response, exception);
    }
}
