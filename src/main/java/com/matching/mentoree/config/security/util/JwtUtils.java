package com.matching.mentoree.config.security.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matching.mentoree.service.dto.ProgramDTO;
import com.matching.mentoree.service.dto.ProgramDTO.ProgramForNavbarDTO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.matching.mentoree.config.security.util.SecurityConstant.ACCESS_TOKEN;
import static com.matching.mentoree.config.security.util.SecurityConstant.REFRESH_TOKEN;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils {

    @Value("${spring.jwt.secret}")
    private final String SECRET_KEY = "secretKey-authorization-jwt-token-for-mentoree";

    private final Long ACCESS_VALIDATION_TIME = 60 * 15 * 1000L; // 15분
    private final Long REFRESH_VALIDATION_TIME = 60 * 60 * 24 * 15 * 1000L; // 15일
    private final CookieUtil cookieUtil;

    public boolean isPresentXToken(HttpServletRequest request, String name) {
        return cookieUtil.getCookie(request, name) != null ? true : false;
    }

    public Authentication getAuthentication(String token) {
        Claims claim = getClaims(token);
        String username = claim.getSubject();
        List<SimpleGrantedAuthority> authorities = Arrays.stream(claim.get("Authorities").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return new UsernamePasswordAuthenticationToken(username, "", authorities);
    }

    public List<ProgramForNavbarDTO> getProgramInfo(String token) {
        Claims claim = getClaims(token);
        List<HashMap> programs = (List<HashMap>) claim.get("participatedPrograms");

        List<ProgramForNavbarDTO> programList = new ArrayList<>();
        for (HashMap program : programs) {
            ProgramForNavbarDTO dto = ProgramForNavbarDTO.builder()
                    .id(Long.parseLong(program.get("id").toString()))
                    .title((String)program.get("title"))
                    .build();
            programList.add(dto);
        }

        return programList;
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

    public String generateAccessToken(Authentication auth, List<ProgramForNavbarDTO> programInfo) {
        return doGenerateAccessToken(auth, ACCESS_VALIDATION_TIME, programInfo);
    }

    public String generateRefreshToken(Authentication auth) {
        return doGenerateRefreshToken(auth, REFRESH_VALIDATION_TIME);
    }

    private String doGenerateRefreshToken(Authentication auth, long validationTime) {
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
        return Jwts.builder()
                .setSubject(username)
                .claim("Authorities", authorities)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + validationTime))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    private String doGenerateAccessToken(Authentication auth, long validationTime, List<ProgramForNavbarDTO> programInfo) {
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

        JSONArray jsonArray = new JSONArray();
        try {
            for (ProgramForNavbarDTO dto : programInfo) {
                JSONObject obj = new JSONObject();
                obj.put("id", dto.getId());
                obj.put("title", dto.getTitle());
                jsonArray.put(obj);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        return Jwts.builder()
                .setSubject(username)
                .claim("Authorities", authorities)
                .claim("participatedPrograms", jsonArray)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + validationTime))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isValidRefreshToken(String refreshToken) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(refreshToken);
            Date now = new Date(System.currentTimeMillis());
            if(now.getTime() - claimsJws.getBody().getExpiration().getTime() < 30000)
                return false;
            return true;
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException exception) {
            return false;
        }
    }
}
