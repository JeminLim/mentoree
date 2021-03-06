package com.mentoree.member.api;

import com.mentoree.config.security.UserPrincipal;
import com.mentoree.config.security.util.AESUtils;
import com.mentoree.config.security.util.EncryptUtils;
import com.mentoree.config.security.util.JwtUtils;
import com.mentoree.config.security.util.SecurityConstant;
import com.mentoree.global.domain.RefreshToken;
import com.mentoree.global.domain.UserRole;
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

    //== ????????? ==//
    @ApiOperation(value = "?????? ????????? ?????? ??? ??????", hidden = true)
    @PostMapping("/login/success")
    public ResponseEntity loginSuccess(HttpServletRequest request, HttpServletResponse response) {
        String email = ((UserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();

        // UUID + IP -> IUWT ?????? + refreshToken ??????
        String encryptedUUID = encryptUtils.encrypt(UUID.randomUUID().toString());
        String encryptedIP = encryptUtils.encrypt(request.getRemoteAddr());
        String accessToken = jwtUtils.generateAccessToken(encryptedUUID, encryptedIP);

        // ?????? ??? UUID ?????? ??????
        setCookie(response, ACCESS_TOKEN_COOKIE, accessToken, REFRESH_VALID_TIME);
        setCookie(response, UUID_COOKIE, encryptedUUID, REFRESH_VALID_TIME);

        //refresh token ??????
        Optional<RefreshToken> refreshToken = tokenRepository.findByEmail(email);
        if(refreshToken.isEmpty()) {
            RefreshToken generateRefreshToken = RefreshToken.builder().uuid(encryptedUUID).accessToken(accessToken).email(email).role(UserRole.USER).build();
            tokenRepository.save(generateRefreshToken);
        } else {
            RefreshToken token = refreshToken.get();
            token.updateRefreshToken(encryptedUUID, accessToken);
            tokenRepository.save(token);
        }
        MemberInfo memberInfo = memberRepository.findMemberInfoByEmail(email).orElseThrow(NoSuchElementException::new);
        List<ParticipatedProgramDTO> participateProgram = participantRepository.findParticipateProgram(email);
        memberInfo.setParticipatedPrograms(participateProgram);

        Map<String, Object> result = new HashMap<>();
        result.put("result", "success");
        result.put("user", memberInfo);
        return ResponseEntity.ok().body(result);
    }


    @ApiOperation(value = "?????? ????????? ?????? ??? ??????", hidden = true)
    @PostMapping("/login/fail")
    public ResponseEntity loginFail() {
        throw new BadCredentialsException("????????? ?????? ???????????? ??????");
    }

    @ApiOperation(value = "???????????? ??????", hidden = true)
    @GetMapping("/logout/success")
    public ResponseEntity logout(HttpServletResponse response) {
        setCookie(response, ACCESS_TOKEN_COOKIE, "", 0);
        setCookie(response, UUID_COOKIE, "", 0);
        Map<String, Object> result = new HashMap<>();
        result.put("result", "success");
        return ResponseEntity.ok().body(result);
    }

    //== ?????? ????????? ==//
    @ApiOperation(value = "????????? ?????? ?????????", notes = "?????? ?????? ???, ????????? ????????? ?????? ??????")
    @PostMapping("/reissue")
    public ResponseEntity reissueToken(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> cookies = Arrays.stream(request.getCookies()).collect(Collectors.toMap(Cookie::getName, Cookie::getValue));
        String accessToken = cookies.get(ACCESS_TOKEN_COOKIE);
        String email = jwtUtils.getSubject(accessToken);

        //== DB ????????? ?????? ==//
        RefreshToken refreshToken = tokenRepository.findByEmail(email).orElseThrow(NoSuchElementException::new);
        String uuidCookie = encryptUtils.decrypt(cookies.get(UUID_COOKIE));
        String dbUUID = encryptUtils.decrypt(refreshToken.getUuid());

        if(!refreshToken.getAccessToken().equals(accessToken) && dbUUID.equals(uuidCookie)) {
            throw new InvalidTokenException("Refresh token ??? ???????????? ????????????.");
        }

        //?????? ??????
        String encryptedUUID = encryptUtils.encrypt(UUID.randomUUID().toString());
        String encryptedIP = encryptUtils.encrypt(request.getRemoteAddr());
        String newToken = jwtUtils.generateAccessToken(encryptedUUID, encryptedIP, email, refreshToken.getRole());

        // ?????? ??? UUID ?????? ??????
        setCookie(response, ACCESS_TOKEN_COOKIE, newToken, REFRESH_VALID_TIME);
        setCookie(response, UUID_COOKIE, encryptedUUID, REFRESH_VALID_TIME);

        //refreshToken ??????
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
