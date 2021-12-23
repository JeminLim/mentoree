package com.mentoree.repository;


import com.mentoree.board.api.dto.BoardDTO;
import com.mentoree.board.domain.Board;
import com.mentoree.board.repository.BoardRepository;
import com.mentoree.member.domain.Member;
import com.mentoree.member.repository.MemberRepository;
import com.mentoree.mission.domain.Mission;
import com.mentoree.mission.repository.MissionRepository;
import com.mentoree.program.domain.Program;
import com.mentoree.program.repository.ProgramRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static com.mentoree.board.api.dto.BoardDTO.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MissionRepository missionRepository;
    @Autowired
    private ProgramRepository programRepository;
    @Autowired
    private MemberRepository memberRepository;

    private Board boardA;
    private Board boardB;
    private Mission mission;
    private Member tester;

    @BeforeEach
    void setUp() {

        tester = Member.builder()
                .email("test@email.com")
                .memberName("testMemberName")
                .nickname("testNickname")
                .userPassword("1234")
                .build();
        memberRepository.save(tester);

        Program testProgram = Program.builder()
                .programName("testProgram")
                .description("test")
                .dueDate(LocalDate.now().plusDays(5))
                .goal("forTest")
                .maxMember(5)
                .build();
        programRepository.save(testProgram);

        mission = Mission.builder()
                .program(testProgram)
                .dueDate(LocalDate.now().plusDays(5))
                .goal("testGoal")
                .content("test content")
                .title("testMission")
                .build();
        missionRepository.save(mission);


        boardA = Board.builder()
                .mission(mission)
                .content("test Content")
                .writer(tester)
                .build();

        boardB = Board.builder()
                .mission(mission)
                .content("test Content22")
                .writer(tester)
                .build();
        boardRepository.save(boardA);
        boardRepository.save(boardB);
    }

    @Test
    @DisplayName("특정 보드 dto로 찾기")
    void findBoardDTOById() {
        BoardInfo findBoard = boardRepository.findBoardInfoById(boardA.getId()).orElseThrow(NoSuchElementException::new);

        assertThat(findBoard.getBoardId()).isEqualTo(boardA.getId());
        assertThat(findBoard.getContent()).isEqualTo(boardA.getContent());
        assertThat(findBoard.getMissionId()).isEqualTo(mission.getId());
        assertThat(findBoard.getMissionTitle()).isEqualTo(mission.getTitle());
        assertThat(findBoard.getWriterNickname()).isEqualTo(tester.getNickname());
    }

    @Test
    @DisplayName("작성된 모든 게시글 dto 반환")
    void findAllBoardById() {
        List<BoardInfo> boards = boardRepository.findAllBoardInfoById(mission.getId());

        assertThat(boards.size()).isEqualTo(2);
        assertThat(boards.get(0).getBoardId()).isEqualTo(boardA.getId());
        assertThat(boards.get(1).getBoardId()).isEqualTo(boardB.getId());

    }
}
