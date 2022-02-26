package com.mentoree.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.member.api.dto.MemberDTO;
import com.mentoree.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.servlet.http.Cookie;
import javax.transaction.Transactional;
import java.util.Map;

import static com.mentoree.config.security.util.SecurityConstant.ACCESS_TOKEN_COOKIE;
import static com.mentoree.config.security.util.SecurityConstant.UUID_COOKIE;
import static com.mentoree.member.api.dto.MemberDTO.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class MemberAPITest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mvc;

    @Autowired
    DataPreparation dataPreparation;

    Map<String, Object> entity;
    Map<String, Cookie> cookie;

    @BeforeEach
    void setUp() {
        dataPreparation.init();
        dataPreparation.initLogin();

        entity = dataPreparation.getEntityMap();
        cookie = dataPreparation.getCookieMap();
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    public void joinMemberTest() throws Exception {
        //given
        MemberRegistRequest registRequest = MemberRegistRequest.builder()
                .email("test@email.com")
                .memberName("tester")
                .nickname("testNick")
                .password("1234qwer!@QW")
                .build();
        String requestBody = objectMapper.writeValueAsString(registRequest);
        //when
        ResultActions response = mvc.perform(
                post("/api/members/join")
                        .contentType("application/json")
                        .content(requestBody)
        );
        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").value("success"));
    }

    @Test
    @DisplayName("회원가입 실패 테스트_중복 데이터")
    public void joinMemberTest_Failure_Duplicate() throws Exception {
        Member member = (Member) entity.get("memberA");
        MemberRegistRequest registRequest = MemberRegistRequest.builder()
                .email(member.getEmail())
                .memberName(member.getMemberName())
                .nickname(member.getNickname())
                .password("1234qwer!@QW")
                .build();
        String requestBody = objectMapper.writeValueAsString(registRequest);
        //when
        ResultActions response = mvc.perform(
                post("/api/members/join")
                        .contentType("application/json")
                        .content(requestBody)
        );
        //then
        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }
    
    @Test
    @DisplayName("유저 프로필 정보")
    public void getUserProfile() throws Exception {
        //given
        Cookie accessToken = this.cookie.get(ACCESS_TOKEN_COOKIE);
        Cookie uuidCookie = this.cookie.get(UUID_COOKIE);
        //when
        ResultActions response = mvc.perform(
                get("/api/members/profile")
                        .header("user-agent", "mobi")
                        .cookie(accessToken, uuidCookie)
                        .param("email", "memberA@email.com")
        );
        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("memberA@email.com"))
                .andExpect(jsonPath("$.memberName").value("memberA"));
    }

    @Test
    @DisplayName("프로필 수정 테스트 성공")
    public void updateProfile() throws Exception {
        //given
        Cookie accessToken = this.cookie.get(ACCESS_TOKEN_COOKIE);
        Cookie uuidCookie = this.cookie.get(UUID_COOKIE);
        Member member = (Member) this.entity.get("memberA");
        MemberInfo updateForm = MemberInfo.builder()
                .email(member.getEmail())
                .nickname("changedNick")
                .memberName(member.getMemberName())
                .link("new link")
                .build();
        String requestBody = objectMapper.writeValueAsString(updateForm);
        //when
        ResultActions response = mvc.perform(
                post("/api/members/profile")
                        .header("user-agent", "mobi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .cookie(accessToken, uuidCookie)
        );

        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(member.getEmail()))
                .andExpect(jsonPath("$.nickname").value("changedNick"));
    }

}
