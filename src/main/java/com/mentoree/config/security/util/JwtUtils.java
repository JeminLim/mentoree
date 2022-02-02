package com.mentoree.config.security.util;

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
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils {

    @Value("${spring.jwt.secret}")
    private final String SECRET_KEY;

    private final Long ACCESS_VALIDATION_TIME = 60 * 15 * 1000L; // 15분
    private final Long REFRESH_VALIDATION_TIME = 60 * 60 * 24 * 15 * 1000L; // 15일

    public Authentication getAuthentication(String token) {
        Claims claim = getClaims(token);
        String username = claim.getSubject();
        List<SimpleGrantedAuthority> authorities = Arrays.stream(claim.get("Authorities").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return new UsernamePasswordAuthenticationToken(username, "", authorities);
    }

    public String getEmail(String refreshToken) {
        Claims claims = getClaims(refreshToken);
        return claims.getSubject();
    }

    private Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token).getBody();

        } catch(ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException exception) {
            throw exception;
        }
    }

    public String generateAccessToken(Authentication auth) {
        return doGenerateToken(auth, ACCESS_VALIDATION_TIME);
    }

    public String generateRefreshToken(Authentication auth) {
        return doGenerateToken(auth, REFRESH_VALIDATION_TIME);
    }

    private String doGenerateToken(Authentication auth, long validationTime) {
        String authorities = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        String username = "";

        if(auth instanceof OAuth2AuthenticationToken) {
            username = (String) ((OAuth2AuthenticationToken) auth).getPrincipal().getAttributes().get("email");;
        }
        else if(auth instanceof UsernamePasswordAuthenticationToken ) {
            username = (String) auth.getPrincipal();
        }
        Key key = getSecretKey();
        return Jwts.builder()
                .setSubject(username)
                .claim("Authorities", authorities)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + validationTime))
                .signWith(key)
                .compact();
    }

    private Key getSecretKey() {
        String secretKeyEncodeBase64 = Encoders.BASE64.encode(SECRET_KEY.getBytes());
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyEncodeBase64);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isValidToken(String token) {
        try {
            String secretKeyEncodeBase64 = Encoders.BASE64.encode(SECRET_KEY.getBytes());
            Jwts.parserBuilder().setSigningKey(secretKeyEncodeBase64).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException exception) {
            return false;
        }
    }
}
