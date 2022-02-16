package com.mentoree.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.config.WebConfig;
import com.mentoree.config.WebSecurityConfig;
import com.mentoree.config.security.JwtFilter;
import com.mentoree.member.api.MemberProfileAPIController;
import com.mentoree.member.api.MemberRegisterAPIController;
import com.mentoree.member.repository.MemberRepository;
import com.mentoree.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.mentoree.member.api.dto.MemberDTO.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = MemberRegisterAPIController.class,
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {WebSecurityConfig.class, WebConfig.class})},
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, OAuth2ClientAutoConfiguration.class}
)
public class MemberRegisterControllerTest {

    @MockBean
    MemberRepository memberRepository;
    @MockBean
    MemberService memberService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;


    @Test
    @DisplayName("회원가입 요청 테스트 성공 컨트롤러 테스트")
    public void joinMemberTest() throws Exception {
        //given
        MemberRegistRequest regForm = MemberRegistRequest.builder()
                .email("test@email.com")
                .memberName("tester")
                .nickname("testNick")
                .password("1234qwer!@QW")
                .build();

        String requestData = objectMapper.writeValueAsString(regForm);

        //when
        ResultActions resultActions = mockMvc.perform(
                post("/api/members/join")
                        .content(requestData)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string("success"));
        verify(memberService, times(1)).join(any());
    }

    @Test
    @DisplayName("회원가입 요청 테스트 실패 컨트롤러 테스트_잘못된 비밀번호 입력 폼")
    public void joinMemberTest_WrongFormatPW() throws Exception {
        //given
        MemberRegistRequest regForm = MemberRegistRequest.builder()
                .email("test@email.com")
                .memberName("tester")
                .nickname("testNick")
                .password("1234!@QW")
                .build();

        String requestData = objectMapper.writeValueAsString(regForm);

        //when
        ResultActions resultActions = mockMvc.perform(
                post("/api/members/join")
                        .content(requestData)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        //then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("이메일 중복 발생 컨트롤러 테스트")
    public void duplicateEmail() throws Exception {
        //given
        when(memberRepository.existsByEmail(any())).thenReturn(true);
        //when
        ResultActions result = mockMvc.perform(
                post("/api/members/join/email-check")
                        .param("email", "test@email.com")
        );
        //then
        result.andExpect(status().isOk())
                .andExpect(content().string("true"));
        verify(memberRepository, times(1)).existsByEmail(any());
    }

    @Test
    @DisplayName("이메일 중복 미발생 컨트롤러 테스트")
    public void noDuplicateEmail() throws Exception {
        //given
        when(memberRepository.existsByEmail(any())).thenReturn(false);
        //when
        ResultActions result = mockMvc.perform(
                post("/api/members/join/email-check")
                        .param("email", "test@email.com")
        );
        //then
        result.andExpect(status().isOk())
                .andExpect(content().string("false"));
        verify(memberRepository, times(1)).existsByEmail(any());
    }

    @Test
    @DisplayName("닉네임 중복 발생 컨트롤러 테스트")
    public void duplicateNickname() throws Exception {
        //given
        when(memberRepository.existsByNickname(any())).thenReturn(true);
        //when
        ResultActions result = mockMvc.perform(
                post("/api/members/join/nickname-check")
                        .param("nickname", "testerNick")
        );
        //then
        result.andExpect(status().isOk())
                .andExpect(content().string("true"));
        verify(memberRepository, times(1)).existsByNickname(any());
    }

    @Test
    @DisplayName("닉네임 중복 미발생 컨트롤러 테스트")
    public void noDuplicateNickname() throws Exception {
        //given
        when(memberRepository.existsByNickname(any())).thenReturn(false);
        //when
        ResultActions result = mockMvc.perform(
                post("/api/members/join/nickname-check")
                        .param("nickname", "testerNick")
        );
        //then
        result.andExpect(status().isOk())
                .andExpect(content().string("false"));
        verify(memberRepository, times(1)).existsByNickname(any());
    }



}
