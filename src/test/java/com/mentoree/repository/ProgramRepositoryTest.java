package com.mentoree.repository;

import com.mentoree.category.domain.Category;
import com.mentoree.category.repository.CategoryRepository;
import com.mentoree.member.domain.Member;
import com.mentoree.member.domain.MemberInterest;
import com.mentoree.member.repository.MemberInterestRepository;
import com.mentoree.member.repository.MemberRepository;
import com.mentoree.participants.domain.Participant;
import com.mentoree.participants.repository.ParticipantRepository;
import com.mentoree.program.domain.Program;
import com.mentoree.program.domain.ProgramRole;
import com.mentoree.program.repository.ProgramRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static com.mentoree.program.api.dto.ProgramDTO.*;
import static org.assertj.core.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProgramRepositoryTest {

    @Autowired
    private ProgramRepository programRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberInterestRepository memberInterestRepository;
    @Autowired
    private ParticipantRepository participantRepository;


    private Program testProgram;
    private Member testMember;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .email("test@email.com")
                .memberName("testMemberName")
                .nickname("testNickname")
                .userPassword("1234")
                .build();

        memberRepository.save(testMember);

        Category testCategory1 = categoryRepository.save(Category.builder().categoryName("testCategory1").build());
        Category testCategory2 = categoryRepository.save(Category.builder().categoryName("testCategory2").build());

        MemberInterest memberInterest1 = MemberInterest.builder().member(testMember).category(testCategory1).build();
        MemberInterest memberInterest2 = MemberInterest.builder().member(testMember).category(testCategory2).build();

        memberInterestRepository.save(memberInterest1);
        memberInterestRepository.save(memberInterest2);


        Category testCategory = categoryRepository.save(Category.builder().categoryName("testCategory").build());
        testProgram = Program.builder()
                .programName("testProgram")
                .description("test")
                .dueDate(LocalDate.now().plusDays(5))
                .goal("forTest")
                .maxMember(5)
                .category(testCategory)
                .build();
        programRepository.save(testProgram);


        Participant participant = Participant.builder().program(testProgram).member(testMember).approval(true).isHost(true).role(ProgramRole.MENTEE).build();

        participantRepository.save(participant);

    }

    @Test
    @DisplayName("프로그램 아이디로 dto 조회 성공")
    void findProgramById() {

        Long targetId = testProgram.getId();

        ProgramInfoDTO findProgram = programRepository.findProgramById(targetId).orElseThrow(NoSuchElementException::new);

        assertThat(findProgram.getId()).isEqualTo(testProgram.getId());
        assertThat(findProgram.getTitle()).isEqualTo(testProgram.getProgramName());
        assertThat(findProgram.getDescription()).isEqualTo(testProgram.getDescription());
        assertThat(findProgram.getDueDate()).isEqualTo(testProgram.getDueDate());
        assertThat(findProgram.getMaxMember()).isEqualTo(testProgram.getMaxMember());
        assertThat(findProgram.getCategory()).isEqualTo(testProgram.getCategory().getCategoryName());
    }

    @Test
    @DisplayName("추천 프로그램 조회 성공")
    void findRecommendProgram() {
        List<Long> participantProgram = new ArrayList<>();
        List<String> interests = new ArrayList<>();
        interests.add("testCategory");

        PageRequest pageRequest = PageRequest.of(0, 5);
        List<ProgramInfoDTO> recommendPrograms = programRepository.findRecommendPrograms(participantProgram, interests, pageRequest).getContent();

        assertThat(recommendPrograms.size()).isEqualTo(1);
        assertThat(recommendPrograms.get(0).getId()).isEqualTo(testProgram.getId());
        assertThat(recommendPrograms.get(0).getTitle()).isEqualTo(testProgram.getProgramName());
        assertThat(recommendPrograms.get(0).getDescription()).isEqualTo(testProgram.getDescription());
        assertThat(recommendPrograms.get(0).getDueDate()).isEqualTo(testProgram.getDueDate());
        assertThat(recommendPrograms.get(0).getMaxMember()).isEqualTo(testProgram.getMaxMember());
    }

    @Test
    @DisplayName("모든 프로그램 조회")
    void findAllProgram() {
        List<Long> participantProgram = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(0, 5);

        List<ProgramInfoDTO> programs = programRepository.findAllProgram(participantProgram, pageRequest).getContent();

        assertThat(programs.size()).isEqualTo(1);
        assertThat(programs.get(0).getId()).isEqualTo(testProgram.getId());
        assertThat(programs.get(0).getTitle()).isEqualTo(testProgram.getProgramName());
        assertThat(programs.get(0).getDescription()).isEqualTo(testProgram.getDescription());
        assertThat(programs.get(0).getDueDate()).isEqualTo(testProgram.getDueDate());
        assertThat(programs.get(0).getMaxMember()).isEqualTo(testProgram.getMaxMember());


    }
}
