package com.mentoree.config.security;

import com.mentoree.config.security.util.JwtUtils;
import com.mentoree.global.exception.InvalidTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private String[] excludePath;

    public void excludePath(String ... path) {
        this.excludePath = path;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            boolean isMatch = false;
            AntPathMatcher pathMatcher = new AntPathMatcher();
            String currentURI = request.getRequestURI();
            for (String path : excludePath) {
                if(pathMatcher.match(path, currentURI)) {
                    isMatch = true;
                    break;
                }
            }
            if(!isMatch) {
                Authentication authentication = jwtUtils.getAuthentication(request);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
            } else {
                filterChain.doFilter(request, response);
            }

        } catch (ExpiredJwtException | InvalidTokenException e) {
            request.setAttribute("AuthException", e);
            throw e;
        }
    }
}
