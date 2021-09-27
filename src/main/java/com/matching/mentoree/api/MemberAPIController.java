package com.matching.mentoree.api;

import com.matching.mentoree.config.security.util.CookieUtil;
import com.matching.mentoree.config.security.util.JwtUtils;
import com.matching.mentoree.repository.ParticipantRepository;
import com.matching.mentoree.service.MemberService;
import com.matching.mentoree.service.dto.ProgramDTO;
import com.matching.mentoree.service.dto.ProgramDTO.ProgramForNavbarDTO;
import com.matching.mentoree.util.CommonUtil;
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

import java.util.List;

import static com.matching.mentoree.config.security.util.SecurityConstant.REFRESH_TOKEN;
import static com.matching.mentoree.util.CommonUtil.*;


@RestController
@RequiredArgsConstructor
public class MemberAPIController {

    private final MemberService memberService;
    private final ParticipantRepository participantRepository;
    private final JwtUtils jwtUtils;
    private final CookieUtil cookieUtil;

    @PostMapping("/api/token")
    @ResponseBody
    public ResponseEntity refreshToken(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = CommonUtil.getLoginEmail();
        List<ProgramForNavbarDTO> programIds = participantRepository.findParticipateProgram(email);
        String msg = "";
        try {
            if (jwtUtils.isPresentXToken(request, REFRESH_TOKEN) && jwtUtils.isValidRefreshToken(request)) {
                String newToken = jwtUtils.generateAccessToken(auth, programIds);
                cookieUtil.addCookie(response, newToken);
                return new ResponseEntity("Token has been published", HttpStatus.OK);
            }
        } catch (Exception e) {
            msg = "Error " + e.getClass() + " has happened: " + e.getMessage();
        }
        return new ResponseEntity(msg, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/member/join/check/email")
    @ResponseBody
    public boolean checkEmail(String email) {
        return memberService.checkEmail(email);
    }


}
