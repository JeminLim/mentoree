package com.mentoree.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.global.advice.response.ErrorCode;
import com.mentoree.global.advice.response.ErrorResponse;
import com.mentoree.global.exception.InvalidTokenException;
import com.nimbusds.common.contenttype.ContentType;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        if(request.getAttribute("AuthException") != null && request.getAttribute("AuthException") instanceof InvalidTokenException) {
            InvalidTokenException exception = (InvalidTokenException) request.getAttribute("AuthException");
            ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.BAD_CREDENTIALS, exception.getMessage());
            setResponse(response, errorResponse);
        }

        if(request.getAttribute("AuthException") != null && request.getAttribute("AuthException") instanceof ExpiredJwtException) {
            ExpiredJwtException exception = (ExpiredJwtException) request.getAttribute("AuthException");
            ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.EXPIRED_TOKEN, exception.getMessage());
            setResponse(response, errorResponse);
        }
    }

    private void setResponse(HttpServletResponse response, ErrorResponse errorResponse) throws IOException {
        response.setContentType(ContentType.APPLICATION_JSON.toString());
        response.setStatus(errorResponse.getStatus());
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }


}
