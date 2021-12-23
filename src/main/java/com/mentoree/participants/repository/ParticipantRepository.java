package com.mentoree.participants.repository;

import com.mentoree.member.domain.Member;
import com.mentoree.participants.domain.Participant;
import com.mentoree.program.domain.Program;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParticipantRepository extends ParticipantCustomRepository, JpaRepository<Participant, Long>{

}
