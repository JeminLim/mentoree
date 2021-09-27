package com.matching.mentoree.service;

import com.matching.mentoree.domain.*;
import com.matching.mentoree.repository.BoardRepository;
import com.matching.mentoree.repository.MissionRepository;
import com.matching.mentoree.service.dto.BoardDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.matching.mentoree.service.dto.BoardDTO.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class BoardServiceTest {

    @Mock
    private MissionRepository missionRepository;
    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    private BoardService boardService;


    private BoardInfo boardDTO;
    private Member member;
    private Mission mission;
    private Board board;

    @BeforeEach
    public void setUp() {
        boardDTO = BoardInfo.builder()
                .content("test content")
                .writerNickname("testNick")
                .missionId(1L)
                .build();

        member = Member.builder()
                .nickname("testNick")
                .memberName("testName")
                .userPassword("1234")
                .email("test@email.com")
                .build();

        mission = Mission.builder()
                .program(Program.builder().programName("test").description("test desc").goal("test goal").maxMember(5).build())
                .content("test mission")
                .dueDate(LocalDateTime.now())
                .title("testTitle")
                .build();

        board = com.matching.mentoree.domain.Board.builder()
                .writer(member)
                .mission(mission)
                .content("content")
                .build();
    }

    @Test
    @DisplayName("멘티 수행 게시판 작성")
    public void write_mentee_board_test() throws Exception {
    }

    @Test
    @DisplayName("멘티 수행 게시판 수정 성공")
    public void update_mentee_board_test() throws Exception {
    }

}