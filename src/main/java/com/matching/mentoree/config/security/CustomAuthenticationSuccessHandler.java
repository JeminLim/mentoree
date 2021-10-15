package com.matching.mentoree.config.security;

import com.matching.mentoree.config.security.util.CookieUtil;
import com.matching.mentoree.config.security.util.JwtUtils;
import com.matching.mentoree.config.security.util.SecurityConstant;
import com.matching.mentoree.domain.RefreshToken;
import com.matching.mentoree.repository.ParticipantRepository;
import com.matching.mentoree.repository.TokenRepository;
import com.matching.mentoree.service.dto.ProgramDTO;
import com.matching.mentoree.service.dto.ProgramDTO.ProgramForNavbarDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

import static com.matching.mentoree.config.security.util.SecurityConstant.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final CookieUtil cookieUtil;
    private final JwtUtils jwtUtils;
    private final TokenRepository tokenRepository;
    private final ParticipantRepository participantRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String email = "";
        if(authentication instanceof OAuth2AuthenticationToken) {
            email = (String) ((OAuth2AuthenticationToken) authentication).getPrincipal().getAttributes().get("email");;
        }
        else if(authentication instanceof UsernamePasswordAuthenticationToken ) {
            email = (String) authentication.getPrincipal();
        }

        List<ProgramForNavbarDTO> programIds = participantRepository.findParticipateProgram(email);
        // 토큰 발급
        String accessToken = jwtUtils.generateAccessToken(authentication, programIds);
        // save access token in cookie
        cookieUtil.addCookie(response, accessToken, ACCESS_TOKEN);

        String refreshToken = jwtUtils.generateRefreshToken(authentication);
        RefreshToken toDB = RefreshToken.builder().email(email).refreshToken(refreshToken).build();
        tokenRepository.save(toDB);
        cookieUtil.addCookie(response, refreshToken, REFRESH_TOKEN);

        // security context holder 에 authentication 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);
        response.sendRedirect("/");
    }

}
