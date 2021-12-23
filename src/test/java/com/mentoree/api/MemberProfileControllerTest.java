package com.mentoree.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.config.WebConfig;
import com.mentoree.config.WebSecurityConfig;
import com.mentoree.config.security.JwtFilter;
import com.mentoree.member.api.MemberProfileAPIController;
import com.mentoree.member.api.MemberRegisterAPIController;
import com.mentoree.member.api.dto.MemberDTO;
import com.mentoree.member.api.dto.MemberDTO.MemberInfo;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = MemberProfileAPIController.class,
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {WebSecurityConfig.class, WebConfig.class, JwtFilter.class})},
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class,
                OAuth2ClientAutoConfiguration.class, OAuth2ResourceServerAutoConfiguration.class}
)
public class MemberProfileControllerTest {
    
    @MockBean
    MemberService memberService;
    @MockBean
    MemberRepository memberRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;
    
    @Test
    @DisplayName("유저 프로파일 정보 GET 컨트롤러 테스트")
    public void getMemberProfile() throws Exception {
        //given
        MemberInfo memberInfo = MemberInfo.builder().email("test@email.com").memberName("tester").nickname("testNick").build();
        when(memberRepository.findMemberInfoByEmail(any())).thenReturn(Optional.of(memberInfo));
        //when
        ResultActions result = mockMvc.perform(
                get("/api/member/profile")
                        .param("email", "test@email.com")
        );
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@email.com"))
                .andExpect(jsonPath("$.memberName").value("tester"))
                .andExpect(jsonPath("$.nickname").value("testNick"));
    }

    @Test
    @DisplayName("유저 프로파일 수정 컨트롤러 테스트")
    public void updateMemberProfile() throws Exception {
        //given
        MemberInfo updateInfo = MemberInfo.builder().email("test@email.com").memberName("tester").nickname("changeNick").build();
        when(memberService.updateMemberInfo(any())).thenReturn(updateInfo);
        String requestData = objectMapper.writeValueAsString(updateInfo);
        //when
        ResultActions result = mockMvc.perform(
                post("/api/member/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestData)
        );
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@email.com"))
                .andExpect(jsonPath("$.memberName").value("tester"))
                .andExpect(jsonPath("$.nickname").value("changeNick"));
    }
    
}
