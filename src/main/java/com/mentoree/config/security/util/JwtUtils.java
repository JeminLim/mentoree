package com.mentoree.config.security.util;

import com.mentoree.config.security.UserPrincipal;
import com.mentoree.global.exception.InvalidTokenException;
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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

import static com.mentoree.config.security.util.SecurityConstant.*;
import static com.mentoree.config.security.util.SecurityConstant.ACCESS_VALIDATION_TIME;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils {

    @Value("${spring.jwt.secret-key}")
    private String SECRET_KEY;

    private static final String IS_MOBILE = "MOBI";
    private final AESUtils aesUtils;


    public Authentication getAuthentication(HttpServletRequest request) {
        try {
            Map<String, String> cookies = Arrays.stream(request.getCookies()).collect(Collectors.toMap(Cookie::getName, Cookie::getValue));
            String accessToken = cookies.get(ACCESS_TOKEN_COOKIE);

            // claim 파싱 -> 토큰의 유효성 검증
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build().parseClaimsJws(accessToken).getBody();

            // pc -> ip 주소 비교 검증
            String userAgent = request.getHeader("User-Agent").toUpperCase();
            if(!userAgent.contains(IS_MOBILE)) {
                String tokenIP = aesUtils.decrypt((String) claims.get("ip"));
                String clientIP = request.getRemoteAddr();

                if(!clientIP.equals(tokenIP)) {
                    throw new InvalidTokenException("외부 IP 접속 의심 클라이언트 입니다.");
                }
            }
            // 토큰 안 UUID 와 UUID 쿠키 비교 검증증
            String tokenUUID = aesUtils.decrypt((String) claims.get("uuid"));
            String cookieUUID = aesUtils.decrypt(cookies.get(UUID_COOKIE));

            if(!tokenUUID.equals(cookieUUID)) {
                throw new InvalidTokenException("비정상적 또는 변조된 토큰입니다");
            }

            List<SimpleGrantedAuthority> authorities = Arrays.stream(claims.get("authorities").toString().split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            UserPrincipal principal = UserPrincipal.builder().email(claims.getSubject()).password("").authorities(authorities).build();

            return new UsernamePasswordAuthenticationToken(principal, "", authorities);

        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException exception) {
            throw new InvalidTokenException("비정상적 또는 변조된 토큰입니다");
        }
    }

    public String generateAccessToken(String encryptedUUID, String encryptedIP) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();

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
                .signWith(getKey(), SignatureAlgorithm.HS512)
                .compact();
    }


   private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
