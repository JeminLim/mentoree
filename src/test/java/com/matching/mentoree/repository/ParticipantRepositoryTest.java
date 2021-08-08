package com.matching.mentoree.repository;

import com.matching.mentoree.domain.Member;
import com.matching.mentoree.domain.Participant;
import com.matching.mentoree.domain.Program;
import com.matching.mentoree.domain.ProgramRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ParticipantRepositoryTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private ParticipantRepository participantRepository;

    @Test
    public void find_participants_by_member_test() throws Exception {
        //given
        Member member = Member.builder().memberName("tester").email("test@email.com").userPassword("1234").nickname("testNick").build();
        em.persist(member);
        Program program = Program.builder().programName("test").maxMember(2).description("test desc").goal("test goal").build();
        em.persist(program);

        Participant testParticipant = Participant.builder()
                .program(program)
                .member(member)
                .isHost(false)
                .role(ProgramRole.MENTEE)
                .approval(false)
                .build();

        participantRepository.save(testParticipant);

        em.flush();
        em.clear();

        //when
        Participant findParticipant = participantRepository.findByMember(member).orElseThrow(NoSuchElementException::new);

        //then
        assertThat(findParticipant.getMember()).isEqualTo(member);
        assertThat(findParticipant.getMember().getUsername()).isEqualTo(member.getUsername());
        assertThat(findParticipant.getMember().getEmail()).isEqualTo(member.getEmail());

    }

    @Test
    public void rejectAll_test() throws Exception {
        //given
        Program program = Program.builder().programName("test").maxMember(2).description("test desc").goal("test goal").build();
        em.persist(program);

        Member member = Member.builder().memberName("tester").email("test@email.com").userPassword("1234").nickname("testNick").build();
        em.persist(member);
        Member member2 = Member.builder().memberName("tester2").email("test2@email.com").userPassword("12345").nickname("testNick2").build();
        em.persist(member);

        Participant testParticipant = Participant.builder()
                .program(program)
                .member(member)
                .isHost(false)
                .role(ProgramRole.MENTEE)
                .approval(false)
                .build();

        Participant testParticipant2 = Participant.builder()
                .program(program)
                .member(member)
                .isHost(false)
                .role(ProgramRole.MENTEE)
                .approval(true)
                .build();
        participantRepository.save(testParticipant);
        participantRepository.save(testParticipant2);
        //when
        participantRepository.rejectAll();
        List<Participant> listAll = participantRepository.findAll();
        //then
        assertThat(listAll.size()).isEqualTo(1);
    }

    @Test
    public void find_participant_history_test() throws Exception {
        //given
        Program program = Program.builder().programName("test").maxMember(2).description("test desc").goal("test goal").build();
        Program program2 = Program.builder().programName("test").maxMember(2).description("test desc").goal("test goal").build();
        Program program3 = Program.builder().programName("test").maxMember(2).description("test desc").goal("test goal").build();
        em.persist(program);
        em.persist(program2);
        em.persist(program3);

        Member member = Member.builder().memberName("tester").email("test@email.com").userPassword("1234").nickname("testNick").build();
        em.persist(member);

        Participant testParticipant = Participant.builder()
                .program(program)
                .member(member)
                .isHost(false)
                .role(ProgramRole.MENTEE)
                .approval(false)
                .build();
        Participant testParticipant2 = Participant.builder()
                .program(program2)
                .member(member)
                .isHost(false)
                .role(ProgramRole.MENTEE)
                .approval(false)
                .build();
        Participant testParticipant3 = Participant.builder()
                .program(program3)
                .member(member)
                .isHost(false)
                .role(ProgramRole.MENTEE)
                .approval(false)
                .build();

        participantRepository.save(testParticipant);
        participantRepository.save(testParticipant2);
        participantRepository.save(testParticipant3);
        //when
        List<Participant> findParticipants = participantRepository.findParticipateHistory(member);

        //then
        assertThat(findParticipants.size()).isEqualTo(3);
    }

}