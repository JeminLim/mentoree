package com.mentoree.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.mission.api.dto.MissionDTOCollection;
import com.mentoree.mission.domain.Mission;
import com.mentoree.program.domain.Program;
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
import java.util.Map;

import static com.mentoree.config.security.util.SecurityConstant.ACCESS_TOKEN_COOKIE;
import static com.mentoree.config.security.util.SecurityConstant.UUID_COOKIE;
import static com.mentoree.mission.api.dto.MissionDTOCollection.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class MissionAPITest {

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
    @DisplayName("현재 미션 리스트 요청 성공")
    public void getMissionList() throws Exception {
        //given
        Program program = (Program) entity.get("programA");

        //when
        ResultActions response = mvc.perform(
                get("/api/missions/list")
                        .header("user-agent", "pc")
                        .cookie(accessToken, uuidCookie)
                        .param("programId", program.getId().toString())
        );
        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.missions.length()").value(1));
    }

    @Test
    @DisplayName("과거 미션 리스트 요청 성공")
    public void getPastMissionList() throws Exception {
        //given
        Program program = (Program) entity.get("programA");

        //when
        ResultActions response = mvc.perform(
                get("/api/missions/list")
                        .header("user-agent", "pc")
                        .cookie(accessToken, uuidCookie)
                        .param("programId", program.getId().toString())
                        .param("isOpen", "false")
        );
        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.missions.length()").value(0));
    }

    @Test
    @DisplayName("미 참가자 미션 요청 실패")
    public void noAuthorityMissionRequest() throws Exception {
        //given
        Program program = (Program) entity.get("programC");

        //when
        ResultActions response = mvc.perform(
                get("/api/missions/list")
                        .header("user-agent", "pc")
                        .cookie(accessToken, uuidCookie)
                        .param("programId", program.getId().toString())
        );
        //then
        response.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("미션 세부 정보 요청 성공")
    public void getMissionInfo() throws Exception {
        //given
        Mission mission = (Mission) entity.get("missionA");
        //when
        ResultActions response = mvc.perform(
                get("/api/missions/" + mission.getId())
                        .header("user-agent", "pc")
                        .cookie(accessToken, uuidCookie)
        );
        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.mission.id").value(mission.getId()))
                .andExpect(jsonPath("$.boardList.length()").value(1));
    }

    @Test
    @DisplayName("미션 작성 테스트")
    public void createMissionTest() throws Exception {
        //given
        Program program = (Program) entity.get("programA");
        MissionCreateRequest requestForm = MissionCreateRequest.builder().content("missionTest")
                .dueDate(LocalDate.now().plusDays(3))
                .goal("testgoal")
                .title("missionTest")
                .programId(program.getId())
                .build();

        String requestBody = objectMapper.writeValueAsString(requestForm);
        //when
        ResultActions response = mvc.perform(
                post("/api/missions/new")
                        .header("user-agent", "pc")
                        .cookie(accessToken, uuidCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );
        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").value("success"));
    }

    @Test
    @DisplayName("미권한자 미션 작성 실패")
    public void noAuthorityUserMissionCreateFail() throws Exception {
        //given
        Program program = (Program) entity.get("programB");
        MissionCreateRequest requestForm = MissionCreateRequest.builder().content("missionTest")
                .dueDate(LocalDate.now().plusDays(3))
                .goal("testgoal")
                .title("missionTest")
                .programId(program.getId())
                .build();

        String requestBody = objectMapper.writeValueAsString(requestForm);
        //when
        ResultActions response = mvc.perform(
                post("/api/missions/new")
                        .header("user-agent", "pc")
                        .cookie(accessToken, uuidCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );
        //then
        response.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").exists());
    }


}
