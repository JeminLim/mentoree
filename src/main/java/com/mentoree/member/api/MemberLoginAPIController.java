package com.mentoree.member.api;

import com.mentoree.config.security.util.AESUtils;
import com.mentoree.config.security.util.JwtUtils;
import com.mentoree.config.security.util.SecurityConstant;
import com.mentoree.global.domain.RefreshToken;
import com.mentoree.global.exception.NoAuthorityException;
import com.mentoree.member.repository.MemberRepository;
import com.mentoree.participants.repository.ParticipantRepository;
import com.mentoree.global.repository.TokenRepository;
import com.mentoree.program.api.dto.ProgramDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

import static com.mentoree.config.security.util.SecurityConstant.*;
import static com.mentoree.program.api.dto.ProgramDTO.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Api("Member Login Controller API")
public class MemberLoginAPIController {

    private final JwtUtils jwtUtils;
    private final AESUtils aesUtils;
    private final TokenRepository tokenRepository;

    //== 로그인 ==//
    @ApiOperation(value = "회원 로그인 성공 후 로직", hidden = true)
    @PostMapping("/login/success")
    public ResponseEntity loginSuccess(HttpServletRequest request, HttpServletResponse response) {
        // UUID + IP -> IUWT 발급
        generateTokenCookie(request, response);

        Map<String, String> result = new HashMap<>();
        result.put("result", "success");
        return ResponseEntity.ok().body(result);
    }


    @ApiOperation(value = "회원 로그인 실패 후 로직", hidden = true)
    @PostMapping("/login/fail")
    public ResponseEntity loginFail() {
        throw new BadCredentialsException("아이디 또는 비밀번호 오류");
    }

    //== 토큰 재발급 ==//
    @ApiOperation(value = "액세스 토큰 재발급", notes = "토큰 검증 후, 재발급 액세스 토큰 반환")
    @PostMapping("/reissue")
    public ResponseEntity reissueToken(HttpServletRequest request, HttpServletResponse response) {

        //== DB 토큰과 검증 ==//
        String email = getUsernameFromAuth();
        RefreshToken refreshToken = tokenRepository.findByEmail(email).orElseThrow(NoSuchElementException::new);

        Map<String, String> cookies = Arrays.stream(request.getCookies()).collect(Collectors.toMap(Cookie::getName, Cookie::getValue));
        String accessToken = cookies.get(ACCESS_TOKEN_COOKIE);

        if(!refreshToken.getAccessToken().equals(accessToken)) {
            throw new NoAuthorityException("비정상 토큰");
        }
        //토큰 갱신
        generateTokenCookie(request, response);

        Map<String, String> result = new HashMap<>();
        result.put("result", "success");
        return ResponseEntity.ok().body(result);
    }



    private String getUsernameFromAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = "";
        if(auth instanceof OAuth2AuthenticationToken) {
            username = (String) ((OAuth2AuthenticationToken) auth).getPrincipal().getAttributes().get("email");;
        }
        else if(auth instanceof UsernamePasswordAuthenticationToken ) {
            username = (String) auth.getPrincipal();
        }
        return username;
    }

    private void generateTokenCookie(HttpServletRequest request, HttpServletResponse response) {
        String encryptedUUID = aesUtils.encrypt(UUID.randomUUID().toString());
        String encryptedIP = aesUtils.encrypt(request.getRemoteAddr());

        String accessToken = jwtUtils.generateAccessToken(encryptedUUID, encryptedIP);

        // 토큰 및 UUID 쿠키 저장
        setCookie(response, ACCESS_TOKEN_COOKIE, accessToken, REFRESH_VALID_TIME);
        setCookie(response, UUID_COOKIE, encryptedUUID, REFRESH_VALID_TIME);
    }

    private void setCookie(HttpServletResponse response, String name, String value, int validationTime) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(validationTime);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }


}
