package com.mentoree.config.security.util;

import com.mentoree.config.security.UserPrincipal;
import com.mentoree.global.domain.UserRole;
import com.mentoree.global.exception.InvalidTokenException;
import com.mentoree.participants.repository.ParticipantRepository;
import com.mentoree.program.api.dto.ProgramDTO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mentoree.config.security.util.SecurityConstant.*;
import static com.mentoree.config.security.util.SecurityConstant.ACCESS_VALIDATION_TIME;
import static com.mentoree.program.api.dto.ProgramDTO.*;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils {

    @Value("${spring.jwt.secret-key}")
    private String SECRET_KEY;

    private static final String IS_MOBILE = "MOBI";
    private final EncryptUtils encryptUtils;
    private final ParticipantRepository participantRepository;


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
                String tokenIP = encryptUtils.decrypt((String) claims.get("ip"));
                String clientIP = request.getRemoteAddr();

                if(!clientIP.equals(tokenIP)) {
                    throw new InvalidTokenException("외부 IP 접속 의심 클라이언트 입니다.");
                }
            }
            // 토큰 안 UUID 와 UUID 쿠키 비교 검증증
            String tokenUUID = encryptUtils.decrypt((String) claims.get("uuid"));
            String cookieUUID = encryptUtils.decrypt(cookies.get(UUID_COOKIE));

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
        String username = ((UserPrincipal) auth.getPrincipal()).getEmail();

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

    public String generateAccessToken(String encryptedUUID, String encryptedIP, String email, UserRole role) {
                String authorities = Stream.of(new SimpleGrantedAuthority(role.getKey()))
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_VALIDATION_TIME))
                .claim("authorities", authorities)
                .claim("uuid", encryptedUUID)
                .claim("ip", encryptedIP)
                .signWith(getKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String getSubject(String token) {
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        JSONParser parser = new JSONParser();
        JSONObject parse = new JSONObject();
        try {
            parse = (JSONObject) parser.parse(payload);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (String)parse.get("sub");
    }

   private Key getKey() {
       return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }
}
