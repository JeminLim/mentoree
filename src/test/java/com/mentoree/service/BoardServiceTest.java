package com.mentoree.service;

import com.mentoree.board.api.dto.BoardDTO;
import com.mentoree.board.api.dto.BoardDTO.BoardInfo;
import com.mentoree.board.domain.Board;
import com.mentoree.board.repository.BoardRepository;
import com.mentoree.board.service.BoardService;
import com.mentoree.category.domain.Category;
import com.mentoree.member.domain.Member;
import com.mentoree.mission.domain.Mission;
import com.mentoree.mission.repository.MissionRepository;
import com.mentoree.program.domain.Program;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BoardServiceTest {

    @Mock
    private MissionRepository missionRepository;
    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    BoardService boardService;

    @Test
    @DisplayName("수행 보드 생성 테스트")
    public void createBoard() throws Exception {
        //given
        Member member = createMember();
        Program program = createProgram();
        Mission mission = createMission(program);
        when(missionRepository.findById(any())).thenReturn(Optional.of(mission));
        BoardInfo saveForm = BoardInfo.builder()
                .content("content")
                .missionId(1L)
                .missionTitle(mission.getTitle())
                .writerNickname(member.getNickname())
                .build();
        //when
        boardService.saveBoard(saveForm, member);

        //then
        verify(boardRepository, times(1)).save(saveForm.toEntity(mission, member));
    }

    @Test
    @DisplayName("수행 보드 수정 테스트")
    public void updateBoard() throws Exception {
        //given
        Member member = createMember();
        Program program = createProgram();
        Mission mission = createMission(program);
        Board board = Board.builder()
                .writer(member)
                .mission(mission)
                .content("originContent")
                .build();
        when(boardRepository.findById(any())).thenReturn(Optional.of(board));
        BoardInfo changedBoard = BoardInfo.builder()
                .writerNickname(member.getNickname())
                .boardId(1L)
                .content("changedContent")
                .missionTitle(mission.getTitle())
                .missionId(1L)
                .build();
        //when
        boardService.updateBoard(changedBoard, 1L);
        //then
        Assertions.assertThat(board.getContent()).isEqualTo(changedBoard.getContent());
    }

    private Program createProgram() {
        return Program.builder()
                .programName("title")
                .description("desc")
                .dueDate(LocalDate.now())
                .goal("goal")
                .maxMember(5)
                .category(Category.builder().categoryName("category").build())
                .build();
    }

    private Mission createMission(Program program) {
        return Mission.builder()
                .program(program)
                .title("title")
                .goal("goal")
                .dueDate(LocalDate.now())
                .content("content")
                .build();
    }

    private Member createMember() {
        return Member.builder()
                .email("test@email.com")
                .memberName("tester")
                .nickname("testNick")
                .userPassword("1234")
                .build();
    }

}
