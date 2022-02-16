package com.mentoree.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.board.api.BoardAPIController;
import com.mentoree.board.repository.BoardRepository;
import com.mentoree.board.service.BoardService;
import com.mentoree.config.WebConfig;
import com.mentoree.config.WebSecurityConfig;
import com.mentoree.mock.WithCustomMockUser;
import com.mentoree.member.domain.Member;
import com.mentoree.member.repository.MemberRepository;
import com.mentoree.participants.repository.ParticipantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static com.mentoree.board.api.dto.BoardDTO.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = BoardAPIController.class,
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {WebSecurityConfig.class, WebConfig.class})}
)
@AutoConfigureMockMvc(addFilters = false)
public class BoardControllerTest {

    @MockBean
    BoardService boardService;
    @MockBean
    BoardRepository boardRepository;
    @MockBean
    MemberRepository memberRepository;
    @MockBean
    ParticipantRepository participantRepository;

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;

    private Member member;
    private BoardInfo boardInfo;

    @BeforeEach
    void setUp() {
        member = Member.builder().email("test@email.com").memberName("tester").nickname("testNick").userPassword("1234").build();
        boardInfo = BoardInfo.builder().content("content").missionId(1L).missionTitle("missionTitle").writerNickname("testNick").build();
    }

    @Test
    @WithCustomMockUser
    @DisplayName("수행보드 정보 가져오기 컨트롤러 테스트")
    public void getBoardInfoTest() throws Exception {
        //given
        when(boardRepository.findBoardInfoById(any())).thenReturn(Optional.of(boardInfo));
        //when
        ResultActions response = mockMvc.perform(get("/api/boards/1"));
        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.boardInfo").exists());
    }

    @Test
    @WithCustomMockUser
    @DisplayName("수행보드 작성 컨트롤러 테스트")
    public void createBoardTest() throws Exception {
        //given
        when(memberRepository.findByEmail(any())).thenReturn(Optional.of(member));
        when(participantRepository.isParticipantByEmailAndMissionId(any(), any())).thenReturn(true);
        String requestBody = objectMapper.writeValueAsString(boardInfo);
        //when
        ResultActions response = mockMvc.perform(
                post("/api/boards/new")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );
        //then
        response.andExpect(status().isOk())
                .andExpect(content().string("success"));
        verify(boardService, times(1)).saveBoard(any(), any());
    }

}
