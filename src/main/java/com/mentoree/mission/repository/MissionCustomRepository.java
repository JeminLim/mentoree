package com.mentoree.mission.repository;

import com.mentoree.mission.api.dto.MissionDTOCollection.MissionDTO;

import java.util.List;
import java.util.Optional;


public interface MissionCustomRepository {
    Optional<MissionDTO> findMissionById(Long missionId);
    List<MissionDTO> findMissionList(Long programId, boolean isOpen);
}
