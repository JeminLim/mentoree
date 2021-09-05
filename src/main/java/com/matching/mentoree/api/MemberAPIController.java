package com.matching.mentoree.api;

import com.matching.mentoree.config.security.util.JwtUtils;
import com.matching.mentoree.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.matching.mentoree.config.security.util.SecurityConstant.REFRESH_TOKEN;


@RestController
@RequiredArgsConstructor
public class MemberAPIController {

    private final MemberService memberService;
    private final JwtUtils jwtUtils;

    @PostMapping("/api/token")
    @ResponseBody
    public ResponseEntity refreshToken(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String msg = "";
        try {
            if (jwtUtils.isPresentXToken(request, REFRESH_TOKEN) && jwtUtils.isValidRefreshToken(request)) {
                String newToken = jwtUtils.generateAccessToken(auth);
                response.setHeader(HttpHeaders.AUTHORIZATION, newToken);
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
