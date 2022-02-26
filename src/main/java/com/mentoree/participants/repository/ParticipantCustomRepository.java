package com.mentoree.participants.repository;

import com.mentoree.participants.domain.Participant;
import com.mentoree.participants.api.dto.ParticipantDTOCollection;

import java.util.List;
import java.util.Optional;

import static com.mentoree.program.api.dto.ProgramDTO.*;

public interface ParticipantCustomRepository {

    List<ParticipantDTOCollection.ApplyRequest> findAllApplicants(Long programId);
    Optional<Participant> findParticipantByMemberIdAndProgram(Long memberId, Long programId);
    List<ParticipatedProgramDTO> findParticipateProgram(String email);
    Optional<Participant> findHost(Long programId);
    Long countCurrentMember(Long programId);
    Boolean isApplicants(Long programId, Long memberId);
    boolean isHost(String email, Long programId);

    boolean isMentor(String email, Long programId);

    boolean isParticipantByEmailAndMissionId(String email, Long missionId);
    boolean isParticipantByEmailAndBoardId(String email, Long boardId);
    boolean isParticipantByEmailAndProgramId(String email, Long programId);



}
