package com.mentoree.repository;

import com.mentoree.category.domain.Category;
import com.mentoree.category.repository.CategoryRepository;
import com.mentoree.member.domain.Member;
import com.mentoree.member.repository.MemberRepository;
import com.mentoree.participants.api.dto.ParticipantDTO;
import com.mentoree.participants.domain.Participant;
import com.mentoree.participants.repository.ParticipantRepository;
import com.mentoree.program.api.dto.ProgramDTO;
import com.mentoree.program.domain.Program;
import com.mentoree.program.domain.ProgramRole;
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
import java.util.Optional;

import static com.mentoree.participants.api.dto.ParticipantDTO.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ParticipantRepositoryTest {

    @Autowired
    private ParticipantRepository participantRepository;


    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProgramRepository programRepository;


    private Member testMemberA;
    private Member testMemberB;
    private Program testProgram;

    @BeforeEach
    void setUp() {
        testMemberA = Member.builder()
                .email("test@email.com")
                .memberName("testMemberName")
                .nickname("testNickname")
                .userPassword("1234")
                .build();
        testMemberB = Member.builder()
                .email("test2@email.com")
                .memberName("testMemberName2")
                .nickname("testNickname2")
                .userPassword("1234")
                .build();
        memberRepository.save(testMemberA);
        memberRepository.save(testMemberB);

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

        Participant applicantA = Participant.builder().program(testProgram).member(testMemberA).role(ProgramRole.MENTEE).approval(true).isHost(true).build();
        Participant applicantB = Participant.builder().program(testProgram).member(testMemberB).role(ProgramRole.MENTEE).build();

        participantRepository.save(applicantA);
        participantRepository.save(applicantB);
    }

    @Test
    @DisplayName("모든 지원자 찾기 성공")
    void findByMember() {
        List<ApplyRequest> allApplicants = participantRepository.findAllApplicants(testProgram.getId());

        assertThat(allApplicants.size()).isEqualTo(1);
        assertThat(allApplicants.get(0).getNickname()).isEqualTo(testMemberB.getNickname());
    }

    @Test
    @DisplayName("특정 프로그램 지원자 찾기 성공")
    void findParticipantByProgramAndMember() {
        Optional<Participant> applicant = participantRepository.findParticipantByMemberIdAndProgram(testMemberB.getId(), testProgram.getId());

        assertThat(applicant).isNotNull();
        assertThat(applicant.get().getMember().getId()).isEqualTo(testMemberB.getId());
        assertThat(applicant.get().getMember().getNickname()).isEqualTo(testMemberB.getNickname());
        assertThat(applicant.get().getMember().getEmail()).isEqualTo(testMemberB.getEmail());
    }

    @Test
    @DisplayName("참가 프로그램 정보 검색")
    void findParticipatedProgram() {
        List<ProgramDTO.ParticipatedProgramDTO> participateProgram = participantRepository.findParticipateProgram(testMemberA.getEmail());

        assertThat(participateProgram.size()).isEqualTo(1);
        assertThat(participateProgram.get(0).getId()).isEqualTo(testProgram.getId());
        assertThat(participateProgram.get(0).getTitle()).isEqualTo(testProgram.getProgramName());
    }

    @Test
    @DisplayName("프로그램 개최자 찾기")
    void findHost() {
        Optional<Participant> host = participantRepository.findHost(testProgram.getId());

        assertThat(host).isNotNull();
        assertThat(host.get().getMember().getId()).isEqualTo(testMemberA.getId());
        assertThat(host.get().getMember().getEmail()).isEqualTo(testMemberA.getEmail());
        assertThat(host.get().getMember().getMemberName()).isEqualTo(testMemberA.getMemberName());
        assertThat(host.get().getMember().getNickname()).isEqualTo(testMemberA.getNickname());
    }

    @Test
    @DisplayName("프로그램 현재 참여중인 멤버 수")
    void countCurrentMember() {
        assertThat(participantRepository.countCurrentMember(testProgram.getId())).isEqualTo(1);
    }

    @Test
    @DisplayName("지원 여부 판별")
    void isApplicants() {
        assertThat(participantRepository.isApplicants(testProgram.getId(), testMemberB.getId())).isTrue();

    }
}
