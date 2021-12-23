package com.mentoree.participants.repository;

import com.mentoree.member.domain.Member;
import com.mentoree.participants.domain.Participant;
import com.mentoree.participants.api.dto.ParticipantDTO;

import java.util.List;
import java.util.Optional;

import static com.mentoree.program.api.dto.ProgramDTO.*;

public interface ParticipantCustomRepository {

    List<ParticipantDTO.ApplyRequest> findAllApplicants(Long programId);
    Optional<Participant> findParticipantByMemberIdAndProgram(Long memberId, Long programId);
    List<ParticipatedProgramDTO> findParticipateProgram(String email);
    Optional<Participant> findHost(Long programId);
    Long countCurrentMember(Long programId);
    Boolean isApplicants(Long programId, Long memberId);


}
