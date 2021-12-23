package com.mentoree.repository;


import com.mentoree.board.domain.Board;
import com.mentoree.board.repository.BoardRepository;
import com.mentoree.global.repository.ReplyRepository;
import com.mentoree.member.domain.Member;
import com.mentoree.member.repository.MemberRepository;
import com.mentoree.mission.domain.Mission;
import com.mentoree.mission.repository.MissionRepository;
import com.mentoree.program.domain.Program;
import com.mentoree.program.repository.ProgramRepository;
import com.mentoree.reply.api.dto.ReplyDTO;
import com.mentoree.reply.domain.Reply;
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

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ReplyRepositoryTest {

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private MissionRepository missionRepository;
    @Autowired
    private ProgramRepository programRepository;
    @Autowired
    private MemberRepository memberRepository;

    private Board board;
    private Mission mission;
    private Member tester;

    private Reply testReply1;
    private Reply testReply2;

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

        board = Board.builder()
                .mission(mission)
                .content("test Content22")
                .writer(tester)
                .build();
        boardRepository.save(board);

        testReply1 = Reply.builder()
                .board(board)
                .content("test reply content")
                .writer(tester)
                .build();
        testReply2 = Reply.builder()
                .board(board)
                .content("test reply content2")
                .writer(tester)
                .build();
        replyRepository.save(testReply1);
        replyRepository.save(testReply2);
    }

    @Test
    @DisplayName("모든 댓글 받기")
    void findAllReplies() {

        List<ReplyDTO> replies = replyRepository.findRepliesAllByBoard(board.getId());

        assertThat(replies.size()).isEqualTo(2);
        assertThat(replies.get(0).getReplyId()).isEqualTo(testReply1.getId());
        assertThat(replies.get(1).getReplyId()).isEqualTo(testReply2.getId());

    }
}
