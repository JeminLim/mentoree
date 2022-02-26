package com.mentoree.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.board.domain.Board;
import com.mentoree.board.repository.BoardRepository;
import com.mentoree.category.domain.Category;
import com.mentoree.config.WebConfig;
import com.mentoree.config.WebSecurityConfig;
import com.mentoree.config.security.JwtFilter;
import com.mentoree.global.repository.ReplyRepository;
import com.mentoree.member.api.MemberProfileAPIController;
import com.mentoree.member.domain.Member;
import com.mentoree.member.repository.MemberRepository;
import com.mentoree.mission.domain.Mission;
import com.mentoree.mock.WithCustomMockUser;
import com.mentoree.participants.repository.ParticipantRepository;
import com.mentoree.program.domain.Program;
import com.mentoree.reply.api.ReplyAPIController;
import com.mentoree.reply.api.dto.ReplyDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(
        controllers = ReplyAPIController.class,
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {WebSecurityConfig.class, WebConfig.class})}
)
public class ReplyControllerTest {

    @MockBean
    ReplyRepository replyRepository;
    @MockBean
    MemberRepository memberRepository;
    @MockBean
    BoardRepository boardRepository;
    @MockBean
    ParticipantRepository participantRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    private Member member;
    private Board board;
    private ReplyDTO replyDTO;
    @BeforeEach
    void setUp() {
        member = Member.builder().email("test@email.com").memberName("tester").nickname("testNick").userPassword("1234").build();

        Category category = Category.builder().categoryName("testCategory").build();
        Program program = Program.builder().programName("testProgram").maxMember(5).goal("testGoal").dueDate(LocalDate.now()).description("testDesc").category(category).build();
        Mission mission = Mission.builder().content("missionContent").program(program).title("missionTitle").dueDate(LocalDate.now()).goal("testGoal").build();

        board = Board.builder().mission(mission).content("content").writer(member).build();
        replyDTO = ReplyDTO.builder().boardId(1L).content("replyContent").writerNickname(member.getNickname()).build();
    }

    @Test
    @WithCustomMockUser
    @DisplayName("댓글 리스트 받아오기 테스트")
    public void getRepliesTest() throws Exception {
        //given
        List<ReplyDTO> replyList = new ArrayList<>();
        replyList.add(replyDTO);
        when(replyRepository.findRepliesAllByBoard(any())).thenReturn(replyList);
        //when
        ResultActions response = mockMvc.perform(
                get("/api/replies/list")
                .param("boardId", "1")
        );
        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$..content").exists());
    }

    @Test
    @WithCustomMockUser
    @DisplayName("댓글 작성 테스트")
    public void replyWriteTest() throws Exception {
        //given
        when(memberRepository.findByEmail(any())).thenReturn(Optional.of(member));
        when(boardRepository.findById(any())).thenReturn(Optional.of(board));
        when(replyRepository.save(any())).thenReturn(replyDTO.toEntity(board, member));
        when(participantRepository.isParticipantByEmailAndBoardId(any(), any())).thenReturn(true);
        String requestBody = objectMapper.writeValueAsString(replyDTO);
        //when
        ResultActions response = mockMvc.perform(
                post("/api/replies/new")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );
        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists());
    }

}
