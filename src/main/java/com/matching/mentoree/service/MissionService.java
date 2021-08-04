package com.matching.mentoree.service;

import com.matching.mentoree.domain.Member;
import com.matching.mentoree.domain.Participant;
import com.matching.mentoree.exception.NoAuthorityException;
import com.matching.mentoree.repository.MissionRepository;
import com.matching.mentoree.repository.ParticipantRepository;
import com.matching.mentoree.service.dto.MissionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;
    private final ParticipantRepository participantRepository;

    @Transactional
    public void createMission(MissionDTO missionDTO, Member member) {

        Participant participant = participantRepository.findByMember(member).orElseThrow(NoSuchElementException::new);
        if(!participant.getRole().getKey().equals("MENTOR"))
            throw new NoAuthorityException("User does not have authority to write mission");
        else {
            missionRepository.save(missionDTO.toEntity());
        }

    }

}
