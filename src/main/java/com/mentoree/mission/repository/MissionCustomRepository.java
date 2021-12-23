package com.mentoree.mission.repository;

import com.mentoree.mission.api.dto.MissionDTO;

import java.util.List;
import java.util.Optional;

public interface MissionCustomRepository {
    Optional<MissionDTO> findMissionById(Long missionId);
    List<MissionDTO> findCurrentMission(Long programId);
    List<MissionDTO> findEndedMission(Long programId);
}
