package com.mentoree.config.security;

import com.mentoree.config.security.util.JwtUtils;
import com.mentoree.participants.repository.ParticipantRepository;
import com.mentoree.global.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;
    private final TokenRepository tokenRepository;
    private final ParticipantRepository participantRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // security context holder 에 authentication 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);
        response.sendRedirect("/api/login/success");
    }

}
