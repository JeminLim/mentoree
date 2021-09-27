package com.matching.mentoree.config.security;

import com.matching.mentoree.config.security.util.JwtUtils;
import com.matching.mentoree.service.dto.ProgramDTO;
import com.matching.mentoree.service.dto.ProgramDTO.ProgramForNavbarDTO;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

import static com.matching.mentoree.config.security.util.SecurityConstant.ACCESS_TOKEN;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(jwtUtils.isPresentXToken(request, ACCESS_TOKEN)) {
            try {
                Authentication authentication = jwtUtils.getAuthentication(request);

                HttpSession httpSession = request.getSession();
                List<ProgramForNavbarDTO> programInfo = jwtUtils.getProgramInfo(request);
                httpSession.setAttribute("participatedPrograms", programInfo);

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
