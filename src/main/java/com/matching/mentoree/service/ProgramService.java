package com.matching.mentoree.service;

import com.matching.mentoree.domain.*;
import com.matching.mentoree.exception.NoAuthorityException;
import com.matching.mentoree.repository.*;
import com.matching.mentoree.service.dto.ProgramDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static com.matching.mentoree.service.dto.ProgramDTO.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgramService {

    private final ProgramRepository programRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipantRepository participantRepository;
    private final MemberRepository memberRepository;

    /**
     * 프로그램 생성
     */
    @Transactional
    public void createProgram(ProgramCreateDTO createDTO, Member login) {

        Category category = categoryRepository.findByCategoryName(createDTO.getCategory()).orElseThrow(NoSuchElementException::new);
        Program program = programRepository.save(createDTO.toEntity(category));

        log.info("createDTO is mentor? = " + createDTO.getProgramRole());

        Participant participant = Participant.builder()
                                    .program(program)
                                    .member(login)
                                    .isHost(true)
                                    .role(ProgramRole.valueOf(createDTO.getProgramRole()))
                                    .approval(true)
                                    .build();
        participantRepository.save(participant);
    }

    /**
     * 프로그램 수정
     */
    @Transactional
    public void updateProgramInfo(ProgramCreateDTO updateForm, Long programId) {
        Program program = programRepository.findById(programId).orElseThrow(NoSuchElementException::new);

        if(updateForm.getProgramName() != null && !updateForm.getProgramName().equals(program.getProgramName()))
            program.updateName(updateForm.getProgramName());

        if(updateForm.getDescription() != null && !updateForm.getDescription().equals(program.getDescription()))
            program.updateDescription(updateForm.getDescription());

        if(updateForm.getTargetNumber() > 0 && updateForm.getTargetNumber() != program.getMaxMember())
            program.updateMaxMember(updateForm.getTargetNumber());

        if(updateForm.getGoal() != null && !updateForm.getGoal().equals(program.getGoal()))
            program.updateGoal(updateForm.getGoal());
    }

    /**
     *  프로그램 참여 신청
     *  return true - 신청 완료 , false - 중복된 신청
     */
    @Transactional
    public boolean applyProgram(Member applyMember, ProgramRole role, Long programId, String message) {
        if(participantRepository.existsByMember(applyMember))
            return false;

        Program targetProgram = programRepository.findById(programId).orElseThrow(NoSuchElementException::new);
        participantRepository.save(Participant.builder()
                .program(targetProgram)
                .member(applyMember)
                .message(message)
                .role(role)
                .isHost(false)
                .approval(false)
                .build());
        return true;
    }

    /**
     * 프로그램 참여 승인
     */
    @Transactional
    public void approval(String email, Long programId) {
        Participant findParticipant = participantRepository.findParticipantByEmailAndProgram(email, programId).orElseThrow(NoSuchElementException::new);
        findParticipant.approve();
    }

    /**
     * 프로그램 참여 거부
     */
    @Transactional
    public void reject(String email, Long programId) {
        Participant findParticipant = participantRepository.findParticipantByEmailAndProgram(email, programId).orElseThrow(NoSuchElementException::new);
        participantRepository.delete(findParticipant);
    }

}
