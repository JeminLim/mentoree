package com.mentoree.mission.service;

import com.mentoree.mission.domain.Mission;
import com.mentoree.program.domain.Program;
import com.mentoree.mission.repository.MissionRepository;
import com.mentoree.program.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static com.mentoree.mission.api.dto.MissionDTOCollection.*;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;
    private final ProgramRepository programRepository;

    @Transactional
    public void createMission(MissionCreateRequest missionDTO) {
        Program program = programRepository.findById(missionDTO.getProgramId()).orElseThrow(NoSuchElementException::new);
        missionRepository.save(missionDTO.toEntity(program));
    }

    @Transactional
    public Mission updateMission(MissionDTO missionDTO) {
        Mission mission = missionRepository.findById(missionDTO.getId()).orElseThrow(NoSuchElementException::new);
        if(missionDTO.getTitle() != null && !missionDTO.getTitle().equals(mission.getTitle()))
            mission.updateTitle(missionDTO.getTitle());

        if(missionDTO.getContent() != null && !missionDTO.getContent().equals(mission.getContent()))
            mission.updateContent(missionDTO.getContent());

        if(missionDTO.getDueDate() != null && !missionDTO.getDueDate().equals(mission.getDueDate()))
            mission.updateDueDate(missionDTO.getDueDate());

        return mission;
    }

    @Transactional
    public void deleteMission(Long missionId) {
        missionRepository.deleteById(missionId);
    }



}
