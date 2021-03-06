package com.mentoree.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.board.repository.BoardRepository;
import com.mentoree.config.WebConfig;
import com.mentoree.config.WebSecurityConfig;
import com.mentoree.config.security.JwtFilter;
import com.mentoree.global.domain.UserRole;
import com.mentoree.member.api.MemberProfileAPIController;
import com.mentoree.mission.api.MissionAPIController;
import com.mentoree.mission.repository.MissionRepository;
import com.mentoree.mission.service.MissionService;
import com.mentoree.mock.WithCustomMockUser;
import com.mentoree.participants.repository.ParticipantRepository;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.mentoree.board.api.dto.BoardDTO.*;
import static com.mentoree.mission.api.dto.MissionDTOCollection.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = MissionAPIController.class,
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {WebSecurityConfig.class, WebConfig.class})}
)
public class MissionControllerTest {

    @MockBean
    MissionService missionService;
    @MockBean
    MissionRepository missionRepository;
    @MockBean
    BoardRepository boardRepository;
    @MockBean
    ParticipantRepository participantRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    private MissionDTO missionDTO;
    private List<MissionDTO> missionDTOList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        missionDTO = MissionDTO.builder().goal("testGoal").dueDate(LocalDate.now()).content("content").id(1L).title("missionTitle").build();
        MissionDTO missionDTO2 = MissionDTO.builder().goal("testGoal2").dueDate(LocalDate.now()).content("content2").id(2L).title("missionTitle2").build();
        missionDTOList.add(missionDTO);
        missionDTOList.add(missionDTO2);
    }

    @Test
    @WithCustomMockUser
    @DisplayName("?????? ?????? ?????? ?????? ?????? ???????????? ?????????")
    public void getCurrentMissionTest() throws Exception {
        //given
        when(missionRepository.findMissionList(anyLong(), anyBoolean())).thenReturn(missionDTOList);
        //when
        ResultActions response = mockMvc.perform(
                get("/api/missions/list")
                        .param("programId", "1")
        );
        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.missions").exists());
    }


    @Test
    @WithCustomMockUser
    @DisplayName("????????? ?????? ?????? ???????????? ?????????")
    public void getPastMissionTest() throws Exception {
        //given
        when(missionRepository.findMissionList(anyLong(), anyBoolean())).thenReturn(missionDTOList);
        //when
        ResultActions response = mockMvc.perform(
                get("/api/missions/list")
                .param("programId", "1")
                .param("isOpen", "false")
        );
        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.missions").exists());
    }

    @Test
    @WithCustomMockUser
    @DisplayName("?????? ?????? ???????????? ???????????? ?????????")
    public void getMissionInfoTest() throws Exception {
        //given
        BoardInfo boardInfo = createBoardInfo(1L, "content", "testNick");
        BoardInfo boardInfo2 = createBoardInfo(2L, "content2", "testNick2");
        List<BoardInfo> boards = new ArrayList<>();
        boards.add(boardInfo);
        boards.add(boardInfo2);

        when(missionRepository.findMissionById(any())).thenReturn(Optional.of(missionDTO));
        when(boardRepository.findAllBoardInfoById(any())).thenReturn(boards);
        //when
        ResultActions response = mockMvc.perform(
                get("/api/missions/1")
        );
        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.mission").exists())
                .andExpect(jsonPath("$.boardList").exists());
    }

    @Test
    @WithCustomMockUser
    @DisplayName("?????? ?????? ???????????? ?????????")
    public void createMissionTest() throws Exception {
        //given
        MissionCreateRequest createRequest = MissionCreateRequest.builder()
                .programId(1L)
                .title("missionTitle")
                .dueDate(LocalDate.now())
                .content("missionContent")
                .goal("missionGoal")
                .build();
        String requestBody = objectMapper.writeValueAsString(createRequest);
        when(participantRepository.isParticipantByEmailAndProgramId(any(),any())).thenReturn(true);
        when(participantRepository.isMentor(any(), any())).thenReturn(true);

        //when
        ResultActions response = mockMvc.perform(
                post("/api/missions/new")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );
        //then
        response.andExpect(status().isOk());
        verify(missionService, times(1)).createMission(any());

    }
    private BoardInfo createBoardInfo(long id, String content, String testNick) {
        return BoardInfo.builder()
                .missionId(1L)
                .missionTitle(missionDTO.getTitle())
                .boardId(id)
                .content(content)
                .writerNickname(testNick)
                .build();
    }


}
