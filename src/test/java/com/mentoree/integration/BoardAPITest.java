package com.mentoree.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.board.api.dto.BoardDTO;
import com.mentoree.board.domain.Board;
import com.mentoree.member.domain.Member;
import com.mentoree.mission.domain.Mission;
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
import java.util.Map;

import static com.mentoree.board.api.dto.BoardDTO.*;
import static com.mentoree.config.security.util.SecurityConstant.ACCESS_TOKEN_COOKIE;
import static com.mentoree.config.security.util.SecurityConstant.UUID_COOKIE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class BoardAPITest {
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
    @DisplayName("게시글 열람 요청")
    public void getBoardTest() throws Exception {
        //given
        Board board = (Board) entity.get("boardA");
        //when
        ResultActions response = mvc.perform(
                get("/api/boards/" + board.getId())
                        .header("user-agent", "pc")
                        .cookie(accessToken, uuidCookie)
        );
        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.boardInfo").exists())
                .andExpect(jsonPath("$.boardInfo.boardId").value(board.getId()));
    }

    @Test
    @DisplayName("미참가자 게시글 열람 요청 실패")
    public void noAuthorityUserGetBoardFail() throws Exception {
        //given
        Board board = (Board) entity.get("boardB");
        //when
        ResultActions response = mvc.perform(
                get("/api/boards/" + board.getId())
                        .header("user-agent", "pc")
                        .cookie(accessToken, uuidCookie)
        );
        //then
        response.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("게시글 작성 성공")
    public void createBoardTest() throws Exception {
        //given
        Mission mission = (Mission) entity.get("missionA");
        Member member = (Member) entity.get("memberA");
        BoardInfo requestForm = BoardInfo.builder().missionTitle(mission.getTitle())
                .missionId(mission.getId())
                .content("board content")
                .writerNickname(member.getNickname())
                .build();
        String requestBody = objectMapper.writeValueAsString(requestForm);
        //when
        ResultActions response = mvc.perform(
                post("/api/boards/new")
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
    @DisplayName("게시글 작성 실패_잘못된 요청 폼")
    public void failCreateBoard() throws Exception {
        //given
        Mission mission = (Mission) entity.get("missionA");
        Member member = (Member) entity.get("memberA");
        BoardInfo requestForm = BoardInfo.builder().missionTitle(mission.getTitle())
                .missionId(mission.getId())
                .writerNickname(member.getNickname())
                .build();
        String requestBody = objectMapper.writeValueAsString(requestForm);
        //when
        ResultActions response = mvc.perform(
                post("/api/boards/new")
                        .header("user-agent", "pc")
                        .cookie(accessToken, uuidCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );
        //then
        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("미권한자 게시글 작성 요청 실패")
    public void noAuthorityCreateBoard() throws Exception {
        //given
        Mission mission = (Mission) entity.get("missionB");
        Member member = (Member) entity.get("memberA");
        BoardInfo requestForm = BoardInfo.builder().missionTitle(mission.getTitle())
                .missionId(mission.getId())
                .content("board content")
                .writerNickname(member.getNickname())
                .build();
        String requestBody = objectMapper.writeValueAsString(requestForm);
        //when
        ResultActions response = mvc.perform(
                post("/api/boards/new")
                        .header("user-agent", "pc")
                        .cookie(accessToken, uuidCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );
        //then
        response.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").exists());
    }



}
