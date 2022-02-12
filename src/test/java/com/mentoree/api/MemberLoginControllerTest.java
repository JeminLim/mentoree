package com.mentoree.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.api.config.TestConfig;
import com.mentoree.config.security.CustomUserDetailService;
import com.mentoree.config.security.util.JwtUtils;
import com.mentoree.global.domain.RefreshToken;
import com.mentoree.global.domain.UserRole;
import com.mentoree.global.repository.TokenRepository;
import com.mentoree.member.api.MemberLoginAPIController;
import com.mentoree.member.domain.Member;
import com.mentoree.member.repository.MemberRepository;
import com.mentoree.participants.repository.ParticipantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.*;

import static com.mentoree.member.api.dto.MemberDTO.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest(
//        controllers = MemberLoginAPIController.class,
//        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {WebSecurityConfig.class, WebConfig.class, JwtFilter.class})},
//        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class,
//                OAuth2ClientAutoConfiguration.class, OAuth2ResourceServerAutoConfiguration.class}
//)
@WebMvcTest(MemberLoginAPIController.class)
@Import(TestConfig.class)
public class MemberLoginControllerTest {

    @MockBean
    MemberRepository memberRepository;
    @MockBean
    JwtUtils jwtUtils;
    @MockBean
    ParticipantRepository participantRepository;
    @MockBean
    TokenRepository tokenRepository;

    @Autowired
    CustomUserDetailService customUserDetailService;

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MemberRegistRequest form = MemberRegistRequest.builder().email("test@email.com").memberName("tester").nickname("testNick").password("1234").build();
        Member member = form.toEntity(passwordEncoder, UserRole.USER);
        when(memberRepository.findByEmail(any())).thenReturn(Optional.of(member));
    }




}
