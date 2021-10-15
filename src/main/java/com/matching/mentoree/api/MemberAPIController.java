package com.matching.mentoree.api;

import com.matching.mentoree.config.security.util.CookieUtil;
import com.matching.mentoree.config.security.util.JwtUtils;
import com.matching.mentoree.domain.RefreshToken;
import com.matching.mentoree.repository.ParticipantRepository;
import com.matching.mentoree.repository.TokenRepository;
import com.matching.mentoree.service.MemberService;
import com.matching.mentoree.service.dto.ProgramDTO;
import com.matching.mentoree.service.dto.ProgramDTO.ProgramForNavbarDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.List;
import java.util.NoSuchElementException;

import static com.matching.mentoree.config.security.util.SecurityConstant.ACCESS_TOKEN;
import static com.matching.mentoree.config.security.util.SecurityConstant.REFRESH_TOKEN;


@RestController
@RequiredArgsConstructor
public class MemberAPIController {

    private final MemberService memberService;
    private final CookieUtil cookieUtil;
    private final JwtUtils jwtUtils;
    private final ParticipantRepository participantRepository;
    private final TokenRepository tokenRepository;

    @PostMapping("/member/join/check/email")
    public boolean checkEmail(String email) {
        return memberService.checkEmail(email);
    }

    @PostMapping("/token/refresh")
    public void publishRefreshToken(HttpServletRequest request, HttpServletResponse response, String accessToken, String refreshToken) {

        Authentication auth = jwtUtils.getAuthentication(refreshToken);
        String email = (String) auth.getPrincipal();
        RefreshToken dbToken = tokenRepository.findByEmail(email).orElseThrow(NoSuchElementException::new);

        if(refreshToken.equals(dbToken) && jwtUtils.isValidRefreshToken(refreshToken)) {
            List<ProgramForNavbarDTO> programIds = participantRepository.findParticipateProgram(email);
            cookieUtil.deleteCookie(request, response, ACCESS_TOKEN);

            HttpSession session = request.getSession();
            String generateAccessToken = jwtUtils.generateAccessToken(auth, programIds);
            cookieUtil.addCookie(response, accessToken, ACCESS_TOKEN);
        }
    }

}
