package com.mentoree.member.api;

import com.mentoree.config.security.UserPrincipal;
import com.mentoree.config.security.util.AESUtils;
import com.mentoree.config.security.util.EncryptUtils;
import com.mentoree.config.security.util.JwtUtils;
import com.mentoree.config.security.util.SecurityConstant;
import com.mentoree.global.domain.RefreshToken;
import com.mentoree.global.exception.InvalidTokenException;
import com.mentoree.global.exception.NoAuthorityException;
import com.mentoree.member.api.dto.MemberDTO;
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
import static com.mentoree.member.api.dto.MemberDTO.*;
import static com.mentoree.program.api.dto.ProgramDTO.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Api("Member Login Controller API")
public class MemberLoginAPIController {

    private final JwtUtils jwtUtils;
    private final EncryptUtils encryptUtils;
    private final TokenRepository tokenRepository;
    private final MemberRepository memberRepository;
    private final ParticipantRepository participantRepository;

    //== 로그인 ==//
    @ApiOperation(value = "회원 로그인 성공 후 로직", hidden = true)
    @PostMapping("/login/success")
    public ResponseEntity loginSuccess(HttpServletRequest request, HttpServletResponse response) {
        // UUID + IP -> IUWT 발급 + refreshToken 저장
        String encryptedUUID = encryptUtils.encrypt(UUID.randomUUID().toString());
        String encryptedIP = encryptUtils.encrypt(request.getRemoteAddr());
        String accessToken = jwtUtils.generateAccessToken(encryptedUUID, encryptedIP);

        // 토큰 및 UUID 쿠키 저장
        setCookie(response, ACCESS_TOKEN_COOKIE, accessToken, REFRESH_VALID_TIME);
        setCookie(response, UUID_COOKIE, encryptedUUID, REFRESH_VALID_TIME);

        //refresh token 저장
        String email = ((UserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
        RefreshToken refreshToken = RefreshToken.builder().uuid(encryptedUUID).accessToken(accessToken).email(email).build();
        tokenRepository.save(refreshToken);

        MemberInfo memberInfo = memberRepository.findMemberInfoByEmail(email).orElseThrow(NoSuchElementException::new);
        List<ParticipatedProgramDTO> participateProgram = participantRepository.findParticipateProgram(email);
        memberInfo.setParticipatedPrograms(participateProgram);

        Map<String, Object> result = new HashMap<>();
        result.put("result", "success");
        result.put("user", memberInfo);
        return ResponseEntity.ok().body(result);
    }


    @ApiOperation(value = "회원 로그인 실패 후 로직", hidden = true)
    @PostMapping("/login/fail")
    public ResponseEntity loginFail() {
        throw new BadCredentialsException("아이디 또는 비밀번호 오류");
    }

    @ApiOperation(value = "로그아웃 로직", hidden = true)
    @GetMapping("/logout/success")
    public ResponseEntity logout(HttpServletResponse response) {
        setCookie(response, ACCESS_TOKEN_COOKIE, "", 0);
        setCookie(response, UUID_COOKIE, "", 0);
        Map<String, Object> result = new HashMap<>();
        result.put("result", "success");
        return ResponseEntity.ok().body(result);
    }

    //== 토큰 재발급 ==//
    @ApiOperation(value = "액세스 토큰 재발급", notes = "토큰 검증 후, 재발급 액세스 토큰 반환")
    @PostMapping("/reissue")
    public ResponseEntity reissueToken(HttpServletRequest request, HttpServletResponse response) {

        //== DB 토큰과 검증 ==//
        String email = ((UserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
        RefreshToken refreshToken = tokenRepository.findByEmail(email).orElseThrow(NoSuchElementException::new);

        Map<String, String> cookies = Arrays.stream(request.getCookies()).collect(Collectors.toMap(Cookie::getName, Cookie::getValue));
        String accessToken = cookies.get(ACCESS_TOKEN_COOKIE);

        String uuidCookie = encryptUtils.decrypt(cookies.get(UUID_COOKIE));
        String dbUUID = encryptUtils.decrypt(refreshToken.getUuid());

        if(!refreshToken.getAccessToken().equals(accessToken) && dbUUID.equals(uuidCookie)) {
            throw new InvalidTokenException("Refresh token 과 일치하지 않습니다.");
        }

        //토큰 갱신
        String encryptedUUID = encryptUtils.encrypt(UUID.randomUUID().toString());
        String encryptedIP = encryptUtils.encrypt(request.getRemoteAddr());
        String newToken = jwtUtils.generateAccessToken(encryptedUUID, encryptedIP);

        // 토큰 및 UUID 쿠키 저장
        setCookie(response, ACCESS_TOKEN_COOKIE, newToken, REFRESH_VALID_TIME);
        setCookie(response, UUID_COOKIE, encryptedUUID, REFRESH_VALID_TIME);

        //refreshToken 갱신
        refreshToken.updateRefreshToken(encryptedUUID, newToken);

        Map<String, String> result = new HashMap<>();
        result.put("result", "success");
        return ResponseEntity.ok().body(result);
    }

    private void setCookie(HttpServletResponse response, String name, String value, int validationTime) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(validationTime);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }


}
