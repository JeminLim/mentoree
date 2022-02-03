package com.mentoree.config.security.util;

import com.mentoree.global.domain.RefreshToken;
import com.mentoree.global.exception.NoAuthorityException;
import com.mentoree.global.repository.TokenRepository;
import com.mentoree.program.api.dto.ProgramDTO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.mentoree.config.security.util.SecurityConstant.*;
import static com.mentoree.config.security.util.SecurityConstant.ACCESS_VALIDATION_TIME;
import static com.mentoree.program.api.dto.ProgramDTO.*;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils {

    @Value("${spring.jwt.secret}")
    private final String SECRET_KEY;

    private static final String IS_MOBILE = "MOBI";
    private final AESUtils aesUtils;

    public Authentication getAuthentication(HttpServletRequest request) {
        Map<String, String> cookies = Arrays.stream(request.getCookies()).collect(Collectors.toMap(Cookie::getName, Cookie::getValue));
        String accessToken = cookies.get(ACCESS_TOKEN_COOKIE);

        Claims claims = Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(accessToken).getBody();
        List<SimpleGrantedAuthority> authorities = Arrays.stream(claims.get("authorities").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return new UsernamePasswordAuthenticationToken(claims.getSubject(), "", authorities);
    }

    public String generateAccessToken(String encryptedUUID, String encryptedIP) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = "";
        if(auth instanceof OAuth2AuthenticationToken) {
            username = (String) ((OAuth2AuthenticationToken) auth).getPrincipal().getAttributes().get("email");;
        }
        else if(auth instanceof UsernamePasswordAuthenticationToken ) {
            username = (String) auth.getPrincipal();
        }

        String authorities = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_VALIDATION_TIME))
                .claim("authorities", authorities)
                .claim("uuid", encryptedUUID)
                .claim("ip", encryptedIP)
                .signWith(getSecretKey())
                .compact();
    }

    public boolean isValidToken(HttpServletRequest request) {
        try {
            Map<String, String> cookies = Arrays.stream(request.getCookies()).collect(Collectors.toMap(Cookie::getName, Cookie::getValue));
            String accessToken = cookies.get(ACCESS_TOKEN_COOKIE);

            // claim 파싱 -> 토큰의 유효성 검증
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build().parseClaimsJws(accessToken).getBody();

            // pc -> ip 주소 비교 검증
            String userAgent = request.getHeader("User-Agent").toUpperCase();
            if(!userAgent.contains(IS_MOBILE)) {
                String tokenIP = aesUtils.decrypt((String) claims.get("ip"));
                String clientIP = request.getRemoteAddr();

                if(!clientIP.equals(tokenIP)) {
                    return false;
                }
            }
            // 토큰 안 UUID 와 UUID 쿠키 비교 검증증
            String tokenUUID = aesUtils.decrypt((String) claims.get("uuid"));
            String cookieUUID = cookies.get(UUID_COOKIE);

            if(!tokenUUID.equals(cookieUUID)) {
                return false;
            }

            return true;
        } catch (ExpiredJwtException exception) {
            throw new RuntimeException("만료된 토큰 입니다. 재요청하세요");
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException exception) {
            throw new RuntimeException("비정상적 토큰입니다. 로그아웃하세요");
        }
    }

   private Key getSecretKey() {
        String secretKeyEncodeBase64 = Encoders.BASE64.encode(SECRET_KEY.getBytes());
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyEncodeBase64);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
