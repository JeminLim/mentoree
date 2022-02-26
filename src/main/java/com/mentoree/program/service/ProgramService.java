package com.mentoree.program.service;

import com.mentoree.category.domain.Category;
import com.mentoree.global.exception.DuplicateExistException;
import com.mentoree.member.domain.Member;
import com.mentoree.category.repository.CategoryRepository;
import com.mentoree.member.repository.MemberRepository;
import com.mentoree.participants.domain.Participant;
import com.mentoree.program.domain.Program;
import com.mentoree.program.domain.ProgramRole;
import com.mentoree.participants.repository.ParticipantRepository;
import com.mentoree.program.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static com.mentoree.program.api.dto.ProgramDTO.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgramService {

    private final ProgramRepository programRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipantRepository participantRepository;

    /**
     * 프로그램 생성
     */
    @Transactional
    public Program createProgram(ProgramCreateDTO createDTO, Member login) {

        if(programRepository.existsByTitle(createDTO.getProgramName())) {
            throw new DuplicateExistException(Program.class, "Duplicate program title already exists");
        }

        Category category = categoryRepository.findByCategoryName(createDTO.getCategory()).orElseThrow(NoSuchElementException::new);
        Program program = programRepository.save(createDTO.toEntity(category));

        Participant participant = Participant.builder()
                                    .program(program)
                                    .member(login)
                                    .isHost(true)
                                    .role(ProgramRole.valueOf(createDTO.getProgramRole()))
                                    .approval(true)
                                    .build();
        participantRepository.save(participant);
        return program;
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

        if(updateForm.getTargetNumber() != null && updateForm.getTargetNumber() != program.getMaxMember())
            program.updateMaxMember(updateForm.getTargetNumber());

        if(updateForm.getGoal() != null && !updateForm.getGoal().equals(program.getGoal()))
            program.updateGoal(updateForm.getGoal());

        if(updateForm.getDueDate() != null && !updateForm.getDueDate().equals(program.getDueDate()))
            program.updateDueDate(updateForm.getDueDate());

    }

    /**
     *  프로그램 참여 신청
     *  return true - 신청 완료 , false - 중복된 신청
     */
    @Transactional
    public boolean applyProgram(Member applyMember, ProgramRole role, Long programId, String message) {
        if(participantRepository.isApplicants(programId, applyMember.getId()))
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
    public void approval(Long memberId, Long programId) {
        Participant findParticipant = participantRepository.findParticipantByMemberIdAndProgram(memberId, programId).orElseThrow(NoSuchElementException::new);
        findParticipant.approve();
    }

    /**
     * 프로그램 참여 거부
     */
    @Transactional
    public void reject(Long memberId, Long programId) {
        Participant findParticipant = participantRepository.findParticipantByMemberIdAndProgram(memberId, programId).orElseThrow(NoSuchElementException::new);
        participantRepository.delete(findParticipant);
    }
}
