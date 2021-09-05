package com.matching.mentoree.config.security;

import com.matching.mentoree.config.security.util.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.matching.mentoree.config.security.util.SecurityConstant.ACCESS_TOKEN;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(jwtUtils.isPresentXToken(request, ACCESS_TOKEN)) {
            try{
                Authentication authentication = jwtUtils.getAuthentication(request);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (ExpiredJwtException e) {
                response.sendError(HttpStatus.UNAUTHORIZED.ordinal(), "Access Token is expired");
            } catch (SignatureException e) {
                response.sendError(HttpStatus.BAD_REQUEST.ordinal(), "Invalid signature is used");
            } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
                response.sendError(HttpStatus.BAD_REQUEST.ordinal(), e.getMessage());
            }
        } else {
            log.info("access token does not present");
        }

        filterChain.doFilter(request, response);
    }
}
