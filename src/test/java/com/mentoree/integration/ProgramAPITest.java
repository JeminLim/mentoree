package com.mentoree.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.category.domain.Category;
import com.mentoree.member.domain.Member;
import com.mentoree.participants.api.dto.ParticipantDTOCollection;
import com.mentoree.program.api.dto.ProgramDTO;
import com.mentoree.program.api.dto.ProgramRequestDTO;
import com.mentoree.program.domain.Program;
import com.mentoree.program.domain.ProgramRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.servlet.http.Cookie;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;

import static com.mentoree.config.security.util.SecurityConstant.ACCESS_TOKEN_COOKIE;
import static com.mentoree.config.security.util.SecurityConstant.UUID_COOKIE;
import static com.mentoree.participants.api.dto.ParticipantDTOCollection.*;
import static com.mentoree.program.api.dto.ProgramDTO.*;
import static com.mentoree.program.api.dto.ProgramRequestDTO.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class ProgramAPITest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mvc;

    @Autowired
    DataPreparation dataPreparation;

    Map<String, Object> entity;
    Map<String, Cookie> cookie;

    Cookie accessToken;
    Cookie uuidCookie;

    @BeforeEach
    void setUp() {
        dataPreparation.init();
        dataPreparation.initLogin();

        entity = dataPreparation.getEntityMap();
        cookie = dataPreparation.getCookieMap();

        accessToken = cookie.get(ACCESS_TOKEN_COOKIE);
        uuidCookie = cookie.get(UUID_COOKIE);
    }
    
    @Test
    @DisplayName("프로그램 생성 테스트 성공")
    public void createProgramTest() throws Exception {
        //given
        Category category = (Category) entity.get("categoryA");
        ProgramCreateDTO requestForm = ProgramCreateDTO.builder().programName("testProgram")
                .dueDate(LocalDate.now().plusDays(5))
                .mentor(false)
                .category(category.getCategoryName())
                .description("testProgramDesc")
                .goal("testGoal")
                .targetNumber(5)
                .build();
        String requestBody = objectMapper.writeValueAsString(requestForm);

        //when
        ResultActions response = mvc.perform(
                post("/api/programs/new")
                        .header("user-agent", "mobi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .cookie(accessToken, uuidCookie)
        );
        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(requestForm.getProgramName()));
    }

    @Test
    @DisplayName("프로그램 전체 리스트 요청")
    public void getProgramList() throws Exception {
        //given
        Program program = (Program) entity.get("programA");
        List<ParticipatedProgramDTO> participatedList = new ArrayList<>();
        participatedList.add(ParticipatedProgramDTO.builder().id(program.getId()).title(program.getProgramName()).build());

        ProgramRequest programRequest = new ProgramRequest(0, participatedList);

        String requestBody = objectMapper.writeValueAsString(programRequest);
        //when
        ResultActions response = mvc.perform(
                post("/api/programs/list")
                        .header("user-agent", "mobi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .cookie(accessToken, uuidCookie)
        );
        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.hasNext").exists())
                .andExpect(jsonPath("$.programList").exists())
                .andExpect(jsonPath("$.programList.length()").value(2));
    }

    @Test
    @DisplayName("추천 프로그램 요청 성공")
    public void getRecommendProgram() throws Exception {
        //given
        Program program = (Program) entity.get("programA");
        List<ParticipatedProgramDTO> participatedList = new ArrayList<>();
        participatedList.add(ParticipatedProgramDTO.builder().id(program.getId()).title(program.getProgramName()).build());
        List<String> interests = Arrays.asList("categoryB");

        RecommendProgramRequest programRequest = new RecommendProgramRequest(0, participatedList, interests);

        String requestBody = objectMapper.writeValueAsString(programRequest);
        //when
        ResultActions response = mvc.perform(
                post("/api/programs/list/recommend")
                        .header("user-agent", "mobi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .cookie(accessToken, uuidCookie)
        );
        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.hasNext").exists())
                .andExpect(jsonPath("$.programRecommendList").exists())
                .andExpect(jsonPath("$.programRecommendList.length()").value(1))
                .andExpect(jsonPath("$.programRecommendList[0].title").value("programB"));
    }

    @Test
    @DisplayName("프로그램 상세정보 요청 성공")
    public void getProgramInfo() throws Exception {
        //given
        Program program = (Program) entity.get("programA");
        //when
        ResultActions response = mvc.perform(
                get("/api/programs/" + program.getId())
                        .header("user-agent", "mobi")
                        .cookie(accessToken, uuidCookie)
        );
        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.programInfo").exists())
                .andExpect(jsonPath("$.programInfo.id").value(program.getId()))
                .andExpect(jsonPath("$.isHost").value(true));
    }

    @Test
    @DisplayName("프로그램 참가 신청")
    public void applyProgramTest() throws Exception {
        //given
        Program program = (Program) entity.get("programC");
        Member member = (Member) entity.get("memberA");
        ApplyRequest applyRequest = new ApplyRequest(null, member.getNickname(), program.getId(), "apply request", ProgramRole.MENTEE);
        String requestBody = objectMapper.writeValueAsString(applyRequest);
        //when
        ResultActions response = mvc.perform(
                post("/api/programs/" + program.getId() + "/join")
                        .header("user-agent", "mobi")
                        .cookie(accessToken, uuidCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );
        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"));
    }

    @Test
    @DisplayName("참가자 리스트 요청 성공")
    public void getApplicants() throws Exception {
        //given
        Program program = (Program) entity.get("programA");

        //when
        ResultActions response = mvc.perform(
                get("/api/programs/" + program.getId() + "/applicants")
                        .header("user-agent", "mobi")
                        .cookie(accessToken, uuidCookie)
        );

        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.programInfo.id").value(program.getId()))
                .andExpect(jsonPath("$.currentNumMember").value(2))
                .andExpect(jsonPath("$.applicants.length()").value(2));
    }

    @Test
    @DisplayName("참가 신청자 승인 성공")
    public void applicantAccept() throws Exception {
        //given
        Program program = (Program) entity.get("programA");
        Member member = (Member) entity.get("memberC");
        Applicant target = new Applicant(member.getId());
        String requestBody = objectMapper.writeValueAsString(target);
        //when
        ResultActions response = mvc.perform(
                post("/api/programs/" + program.getId() + "/applicants/accept")
                        .header("user-agent", "mobi")
                        .cookie(accessToken, uuidCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );
        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"));
    }

    @Test
    @DisplayName("참가 신청자 거절 성공")
    public void applicantReject() throws Exception {
        //given
        Program program = (Program) entity.get("programA");
        Member member = (Member) entity.get("memberD");
        Applicant target = new Applicant(member.getId());
        String requestBody = objectMapper.writeValueAsString(target);
        //when
        ResultActions response = mvc.perform(
                post("/api/programs/" + program.getId() + "/applicants/reject")
                        .header("user-agent", "mobi")
                        .cookie(accessToken, uuidCookie)
//                        .param("memberId", member.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );
        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"));
    }

    @Test
    @DisplayName("미권한자 참가 신청 관리 실패")
    public void noAuthorityManageApplicants() throws Exception {
        //given
        Program program = (Program) entity.get("programB");
        Member member = (Member) entity.get("memberA");
        Applicant target = new Applicant(member.getId());
        String requestBody = objectMapper.writeValueAsString(target);
        //when
        ResultActions response = mvc.perform(
                post("/api/programs/" + program.getId() + "/applicants/accept")
                        .header("user-agent", "mobi")
                        .cookie(accessToken, uuidCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );
        //then
        response.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("프로그램 개최자가 아닙니다"));
    }

}
