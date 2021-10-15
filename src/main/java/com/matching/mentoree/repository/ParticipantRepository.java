package com.matching.mentoree.repository;

import com.matching.mentoree.domain.Member;
import com.matching.mentoree.domain.Participant;
import com.matching.mentoree.domain.Program;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParticipantRepository extends ParticipantCustomRepository, JpaRepository<Participant, Long>{

    Optional<Participant> findByMember(Member member);
    Optional<Participant> findByMemberAndProgram(Member member, Program program);
    boolean existsByMember(Member member);

}
