package com.matching.mentoree.config.security;

import com.matching.mentoree.config.security.util.CookieUtil;
import com.matching.mentoree.config.security.util.JwtUtils;
import com.matching.mentoree.domain.RefreshToken;
import com.matching.mentoree.repository.TokenRepository;
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
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.matching.mentoree.config.security.util.SecurityConstant.ACCESS_TOKEN;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final CookieUtil cookieUtil;
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String refreshRequestURI = "/token/refresh";

        if(!refreshRequestURI.equals(path)) {
            if (jwtUtils.isPresentXToken(request, ACCESS_TOKEN)) {
                try {
                    Cookie atCookie = cookieUtil.getCookie(request, ACCESS_TOKEN).orElseThrow(NoSuchElementException::new);
                    String accessToken = atCookie.getValue();

                    Authentication authentication = jwtUtils.getAuthentication(accessToken);

                    List<ProgramForNavbarDTO> programInfo = jwtUtils.getProgramInfo(accessToken);
                    HttpSession httpSession = request.getSession();
                    httpSession.setAttribute("participatedPrograms", programInfo);

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                } catch (ExpiredJwtException e) {
                    response.sendError(HttpStatus.UNAUTHORIZED.ordinal(), "Invalid signature is used");
                } catch (SignatureException e) {
                    response.sendError(HttpStatus.BAD_REQUEST.ordinal(), "Invalid signature is used");
                } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
                    response.sendError(HttpStatus.BAD_REQUEST.ordinal(), e.getMessage());
                }
            }

            filterChain.doFilter(request, response);
        }
    }
}
