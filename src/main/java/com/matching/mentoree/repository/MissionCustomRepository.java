package com.matching.mentoree.repository;

import com.matching.mentoree.service.dto.MissionDTO;

import java.util.List;

public interface MissionCustomRepository {

    List<MissionDTO> findCurrentMission(Long programId);
    List<MissionDTO> findEndedMission(Long programId);
}
