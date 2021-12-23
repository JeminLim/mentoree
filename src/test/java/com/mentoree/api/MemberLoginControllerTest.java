package com.mentoree.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.api.config.TestConfig;
import com.mentoree.config.WebConfig;
import com.mentoree.config.WebSecurityConfig;
import com.mentoree.config.security.CustomUserDetailService;
import com.mentoree.config.security.JwtFilter;
import com.mentoree.config.security.UserPrincipal;
import com.mentoree.config.security.util.JwtUtils;
import com.mentoree.global.domain.RefreshToken;
import com.mentoree.global.domain.UserRole;
import com.mentoree.global.repository.TokenRepository;
import com.mentoree.member.api.MemberLoginAPIController;
import com.mentoree.member.api.MemberRegisterAPIController;
import com.mentoree.member.api.dto.MemberDTO;
import com.mentoree.member.domain.Member;
import com.mentoree.member.repository.MemberRepository;
import com.mentoree.member.service.MemberService;
import com.mentoree.participants.domain.Participant;
import com.mentoree.participants.repository.ParticipantRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.JsonPathResultMatchers;
import org.thymeleaf.dialect.IExecutionAttributeDialect;

import java.sql.Ref;
import java.util.*;

import static com.mentoree.member.api.dto.MemberDTO.*;
import static com.mentoree.member.api.dto.MemberDTO.RegistrationRequest;
import static org.assertj.core.api.Assertions.assertThat;
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
        RegistrationRequest form = RegistrationRequest.builder().email("test@email.com").memberName("tester").nickname("testNick").password("1234").build();
        Member member = form.toEntity(passwordEncoder, UserRole.USER);
        when(memberRepository.findByEmail(any())).thenReturn(Optional.of(member));
    }


    @Test
    @DisplayName("로그인 성공 컨트롤러 테스트")
    @WithUserDetails(value="test@email.com", userDetailsServiceBeanName = "customUserDetailService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void loginSuccess() throws Exception {
        //given
        MemberInfo user = MemberInfo.builder().email("test@email.com").memberName("tester").nickname("testNick").build();
        when(jwtUtils.generateAccessToken(any())).thenReturn("accessToken");
        when(jwtUtils.generateRefreshToken(any())).thenReturn("refreshToken");
        when(memberRepository.findMemberInfoByEmail(any())).thenReturn(Optional.of(user));
        when(participantRepository.findParticipateProgram(any())).thenReturn(new ArrayList<>());

        //when
        ResultActions result = mockMvc.perform(
                post("/api/login/success")
        );
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.user.email").value(user.getEmail()))
                .andExpect(jsonPath("$.user.memberName").value(user.getMemberName()))
                .andExpect(jsonPath("$.user.nickname").value(user.getNickname()))
                .andExpect(jsonPath("accessToken").value("accessToken"))
                .andExpect(jsonPath("refreshToken").value("refreshToken"));
    }

    @Test
    @DisplayName("로그인 실패 결과 컨트롤러 테스트")
    public void loginFail() throws Exception {
        mockMvc.perform(post("/api/login/fail"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("토큰 재발급 성공 컨트롤러 테스트")
    public void reissue_token_success() throws Exception {
        //given
        RefreshToken refreshToken = RefreshToken.builder().refreshToken("refreshToken").email("test@email.com").build();
        when(jwtUtils.getEmail(any())).thenReturn("test@email.com");
        when(tokenRepository.findByEmail(any())).thenReturn(Optional.of(refreshToken));
        when(jwtUtils.isValidToken(any())).thenReturn(true);
        when(jwtUtils.generateAccessToken(any())).thenReturn("refreshToken");

        Map<String, String> testToken = new HashMap<>();
        testToken.put("refreshToken", "refreshToken");
        String requestData = objectMapper.writeValueAsString(testToken);

        //when
        ResultActions result = mockMvc.perform(
                post("/api/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestData)
        );

        //then
        result.andExpect(status().isOk())
                .andExpect(content().string("refreshToken"));

    }

    @Test
    @DisplayName("토큰 재발급 실패_Invalid refresh token 컨트롤러 테스트")
    public void reissue_token_fail() throws Exception {
        //given
        RefreshToken refreshToken = RefreshToken.builder().refreshToken("refreshToken").email("test@email.com").build();
        when(jwtUtils.getEmail(any())).thenReturn("test@email.com");
        when(tokenRepository.findByEmail(any())).thenReturn(Optional.of(refreshToken));
        when(jwtUtils.isValidToken(any())).thenReturn(false);
        when(jwtUtils.generateAccessToken(any())).thenReturn("refreshToken");

        Map<String, String> testToken = new HashMap<>();
        testToken.put("refreshToken", "refreshToken");
        String requestData = objectMapper.writeValueAsString(testToken);

        //when
        ResultActions result = mockMvc.perform(
                post("/api/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestData)
        );

        //then
        result.andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid Token"));
    }

    @Test
    @DisplayName("토큰 재발급 실패_다른 Refresh token 첨부 컨트롤러 테스트")
    public void reissue_token_fail_wrong_token() throws Exception {
        //given
        RefreshToken refreshToken = RefreshToken.builder().refreshToken("refreshToken").email("test@email.com").build();
        when(jwtUtils.getEmail(any())).thenReturn("test@email.com");
        when(tokenRepository.findByEmail(any())).thenReturn(Optional.of(refreshToken));
        when(jwtUtils.isValidToken(any())).thenReturn(true);
        when(jwtUtils.generateAccessToken(any())).thenReturn("refreshToken");

        Map<String, String> testToken = new HashMap<>();
        testToken.put("refreshToken", "wrongRefreshToken");
        String requestData = objectMapper.writeValueAsString(testToken);

        //when
        ResultActions result = mockMvc.perform(
                post("/api/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestData)
        );

        //then
        result.andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid Token"));
    }




}
