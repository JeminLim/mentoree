package com.mentoree.config.security;

import com.mentoree.config.security.util.JwtUtils;
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
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
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
                if(pathMatcher.match(currentURI, path)) {
                    isMatch = true;
                    break;
                }
            }

            if(!isMatch) {
                if (jwtUtils.isValidToken(request)) {
                    Authentication authentication = jwtUtils.getAuthentication(request);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
                filterChain.doFilter(request, response);
            } else {
                filterChain.doFilter(request, response);
            }

        } catch (ExpiredJwtException e) {
            response.sendError(HttpStatus.UNAUTHORIZED.ordinal(), "Invalid signature is used");
        } catch (SignatureException e) {
            response.sendError(HttpStatus.BAD_REQUEST.ordinal(), "Invalid signature is used");
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            response.sendError(HttpStatus.BAD_REQUEST.ordinal(), e.getMessage());
        }
    }
}
