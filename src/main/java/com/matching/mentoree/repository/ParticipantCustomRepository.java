package com.matching.mentoree.repository;

import com.matching.mentoree.domain.Member;
import com.matching.mentoree.domain.Participant;
import com.matching.mentoree.domain.Program;
import com.matching.mentoree.service.dto.ProgramDTO;

import java.util.List;
import java.util.Optional;

import static com.matching.mentoree.service.dto.ProgramDTO.*;

public interface ParticipantCustomRepository {

    void rejectAll();
    List<Participant> findParticipateHistory(Member targetMember);
    List<Participant> findAllApplicants(Program program);
    Optional<Participant> findParticipantByEmailAndProgram(String email, Long id);
    List<Participant> findMentor(Long programId);
    List<ProgramForNavbarDTO> findParticipateProgram(String email);
    Optional<Participant> findHost(Long programId);

}
