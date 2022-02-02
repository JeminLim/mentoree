package com.mentoree.member.api;

import com.mentoree.config.security.util.JwtUtils;
import com.mentoree.global.domain.RefreshToken;
import com.mentoree.member.repository.MemberRepository;
import com.mentoree.participants.repository.ParticipantRepository;
import com.mentoree.global.repository.TokenRepository;
import com.mentoree.member.api.dto.MemberDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.mentoree.program.api.dto.ProgramDTO.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Api("Member Login Controller API")
public class MemberLoginAPIController {

    private final JwtUtils jwtUtils;
    private final ParticipantRepository participantRepository;
    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;

    //== 로그인 ==//
    @ApiOperation(value = "회원 로그인 성공 후 로직", hidden = true)
    @PostMapping("/login/success")
    public ResponseEntity loginSuccess() {
        log.info("login success ... " );

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("authentication principal={} ", authentication.getPrincipal());
        String accessToken = jwtUtils.generateAccessToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(authentication);

        String email = "";
        if(authentication instanceof OAuth2AuthenticationToken) {
            email = (String) ((OAuth2AuthenticationToken) authentication).getPrincipal().getAttributes().get("email");;
        }
        else if(authentication instanceof UsernamePasswordAuthenticationToken ) {
            UserDetails principal = (UserDetails) authentication.getPrincipal();
            email = principal.getUsername();
        }

        RefreshToken toDB = RefreshToken.builder().email(email).refreshToken(refreshToken).build();
        tokenRepository.save(toDB);

        MemberDTO.MemberInfo loginMemberInfo = memberRepository.findMemberInfoByEmail(email).orElseThrow(NoSuchElementException::new);
        List<ParticipatedProgramDTO> participateProgram = participantRepository.findParticipateProgram(email);
        loginMemberInfo.setParticipatedPrograms(participateProgram);

        Map<String, Object> data = new HashMap<>();
        data.put("user", loginMemberInfo);
        data.put("accessToken", accessToken);
        data.put("refreshToken", refreshToken);
        return ResponseEntity.ok().body(data);
    }

    @ApiOperation(value = "회원 로그인 실패 후 로직", hidden = true)
    @PostMapping("/login/fail")
    public ResponseEntity loginFail() {
        throw new BadCredentialsException("아이디 또는 비밀번호 오류");
    }

    //== 토큰 재발급 ==//
    @ApiOperation(value = "액세스 토큰 재발급", notes = "토큰 검증 후, 재발급 액세스 토큰 반환")
    @PostMapping("/reissue")
    public ResponseEntity publishRefreshToken(@RequestBody Map<String, String> token) {
        String refreshToken = token.get("refreshToken");
        String loginEmail = jwtUtils.getEmail(refreshToken);
        log.info("loginEmail = " + loginEmail);

        RefreshToken tokenFromDB = tokenRepository.findByEmail(loginEmail).orElseThrow(NoSuchElementException::new);
        log.info("tokenFromDB = " + tokenFromDB.getRefreshToken());
        if(refreshToken.equals(tokenFromDB.getRefreshToken()) && jwtUtils.isValidToken(refreshToken)) {
            log.info("valid");
            String newAccessToken = jwtUtils.generateAccessToken(jwtUtils.getAuthentication(refreshToken));
            return ResponseEntity.ok().body(newAccessToken);
        } else {
            log.info("invalid");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Token");
        }
    }


}
