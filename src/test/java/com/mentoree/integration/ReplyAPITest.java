package com.mentoree.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.board.domain.Board;
import com.mentoree.member.domain.Member;
import com.mentoree.reply.api.dto.ReplyDTO;
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
public class ReplyAPITest {

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
    @DisplayName("게시글 댓글 리스트 요청")
    public void getRepliesTest() throws Exception {
        //given
        Board board = (Board) entity.get("boardA");
        //when
        ResultActions response = mvc.perform(
                get("/api/replies/list")
                        .header("user-agent", "pc")
                        .cookie(accessToken, uuidCookie)
                        .param("boardId", board.getId().toString())
        );
        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
    
    @Test
    @DisplayName("미권한자 댓글 리스트 요청")
    public void noAuthorityGetReplies() throws Exception {
        //given
        Board board = (Board) entity.get("boardB");
        //when
        ResultActions response = mvc.perform(
                get("/api/replies/list")
                        .header("user-agent", "pc")
                        .cookie(accessToken, uuidCookie)
                        .param("boardId", board.getId().toString())
        );
        //then
        response.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("댓글 작성 요청 성공")
    public void createReplyTest() throws Exception {
        //given
        Board board = (Board) entity.get("boardA");
        Member member = (Member) entity.get("memberA");
        ReplyDTO requestForm = ReplyDTO.builder().boardId(board.getId())
                .content("replyContent")
                .writerNickname(member.getNickname())
                .build();
        String requestBody = objectMapper.writeValueAsString(requestForm);
        //when
        ResultActions response = mvc.perform(
                post("/api/replies/new")
                        .header("user-agent", "pc")
                        .cookie(accessToken, uuidCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );
        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.boardId").value(board.getId()))
                .andExpect(jsonPath("$.writerNickname").value(member.getNickname()));
    }

    @Test
    @DisplayName("미권한자 댓글 작성 요청 실패")
    public void noAuthorityCreateReplyFail() throws Exception {
        //given
        Board board = (Board) entity.get("boardB");
        Member member = (Member) entity.get("memberA");
        ReplyDTO requestForm = ReplyDTO.builder().boardId(board.getId())
                .content("replyContent")
                .writerNickname(member.getNickname())
                .build();
        String requestBody = objectMapper.writeValueAsString(requestForm);
        //when
        ResultActions response = mvc.perform(
                post("/api/replies/new")
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
