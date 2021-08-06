package com.matching.mentoree.service;

import com.matching.mentoree.domain.Member;
import com.matching.mentoree.domain.Mission;
import com.matching.mentoree.domain.Participant;
import com.matching.mentoree.domain.Program;
import com.matching.mentoree.exception.NoAuthorityException;
import com.matching.mentoree.repository.MissionRepository;
import com.matching.mentoree.repository.ParticipantRepository;
import com.matching.mentoree.repository.ProgramRepository;
import com.matching.mentoree.service.dto.MissionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;
    private final ProgramRepository programRepository;

    @Transactional
    public void createMission(MissionDTO missionDTO, Member member) {
        Program program = programRepository.findById(missionDTO.getProgramId()).orElseThrow(NoSuchElementException::new);
        missionRepository.save(missionDTO.toEntity(program));
    }

    @Transactional
    public void updateMission(MissionDTO missionDTO, Long missionId) {
        Mission mission = missionRepository.findById(missionId).orElseThrow(NoSuchElementException::new);

        if(missionDTO.getTitle() != null && !missionDTO.getTitle().equals(mission.getTitle()))
            mission.updateTitle(missionDTO.getTitle());

        if(missionDTO.getContent() != null && !missionDTO.getContent().equals(mission.getContent()))
            mission.updateContent(missionDTO.getContent());

        if(missionDTO.getDueDate() != null && !missionDTO.getDueDate().equals(mission.getDueDate()))
            mission.updateDueDate(missionDTO.getDueDate());
    }

    @Transactional
    public void deleteMission(Long missionId) {
        missionRepository.deleteById(missionId);
    }



}
