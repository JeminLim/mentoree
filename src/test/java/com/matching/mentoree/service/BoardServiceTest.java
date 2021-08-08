package com.matching.mentoree.service;

import com.matching.mentoree.domain.*;
import com.matching.mentoree.repository.MenteeBoardRepository;
import com.matching.mentoree.repository.MentorBoardRepository;
import com.matching.mentoree.repository.MissionRepository;
import com.matching.mentoree.service.dto.BoardDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class BoardServiceTest {

    @Mock
    private MissionRepository missionRepository;
    @Mock
    private MenteeBoardRepository menteeBoardRepository;
    @Mock
    private MentorBoardRepository mentorBoardRepository;

    @InjectMocks
    private BoardService boardService;


    private BoardDTO boardDTO;
    private Member member;
    private Mission mission;
    private MenteeBoard menteeBoard;

    @BeforeEach
    public void setUp() {
        boardDTO = BoardDTO.builder()
                .menteeBoardId(1L)
                .content("test content")
                .memberId(1L)
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

        menteeBoard = MenteeBoard.builder()
                .writer(member)
                .mission(mission)
                .content("content")
                .build();
    }

    @Test
    @DisplayName("멘티 수행 게시판 작성")
    public void write_mentee_board_test() throws Exception {
        //given
        given(missionRepository.findById(boardDTO.getMissionId())).willReturn(Optional.of(mission));
        //when
        boardService.saveMenteeBoard(boardDTO, member);
        //then
        verify(menteeBoardRepository).save(boardDTO.toMenteeBoardEntity(mission, member));
    }

    @Test
    @DisplayName("멘토 피드백 작성 성공")
    public void write_mentor_board_test() throws Exception {
        //given
        given(menteeBoardRepository.findById(boardDTO.getMenteeBoardId())).willReturn(Optional.of(menteeBoard));
        //when
        boardService.saveMentorBoard(boardDTO, member);
        //then
        verify(mentorBoardRepository).save(boardDTO.toMentorBoardEntity(menteeBoard, member));
    }
    
    @Test
    @DisplayName("멘티 수행 게시판 수정 성공")
    public void update_mentee_board_test() throws Exception {
        //given
        BoardDTO changedDTO = BoardDTO.builder()
                .content("changed content")
                .build();

        given(menteeBoardRepository.findById(any())).willReturn(Optional.of(menteeBoard));
        //when
        boardService.updateMenteeBoard(changedDTO, 1L);
        
        //then
        assertThat(menteeBoard.getContent()).isEqualTo(changedDTO.getContent());
    }

    @Test
    @DisplayName("멘토 피드백 수정 성공")
    public void update_mentor_board_test() throws Exception {
        //given
        BoardDTO changedDTO = BoardDTO.builder()
                .content("changed content")
                .build();

        MentorBoard mentorBoard = MentorBoard.builder()
                .menteeBoard(menteeBoard)
                .feedback("origin content")
                .writer(member)
                .build();

        given(mentorBoardRepository.findById(any())).willReturn(Optional.of(mentorBoard));
        //when
        boardService.updateMentorBoard(changedDTO, 1L);

        //then
        assertThat(mentorBoard.getFeedback()).isEqualTo(changedDTO.getContent());
    }

}