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

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgramService {

    private final ProgramRepository programRepository;
    private final CategoryRepository categoryRepository;
    private final ProgramCategoryRepository programCategoryRepository;
    private final ParticipantRepository participantRepository;
    private final MemberRepository memberRepository;

    /**
     * 프로그램 생성
     */
    @Transactional
    public void createProgram(ProgramDTO createDTO, Member login) {

        Program program = programRepository.save(createDTO.toEntity());

        List<Category> categories = createDTO.getCategories().stream()
                                    .map(c -> Category.builder().categoryName(c).build())
                                    .collect(Collectors.toList());

        categories.stream().forEach(c -> categoryRepository.save(c));

        categories.stream().forEach(c ->
                programCategoryRepository.save(ProgramCategory.builder()
                        .category(c)
                        .program(program)
                        .build())
        );

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
    public void updateProgramInfo(ProgramDTO updateForm, Long programId) {
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
     */
    @Transactional
    public void applyProgram(Member applyMember, String role, Long programId) {
        Program targetProgram = programRepository.findById(programId).orElseThrow(NoSuchElementException::new);

        participantRepository.save(Participant.builder()
                .program(targetProgram)
                .member(applyMember)
                .role(ProgramRole.valueOf(role))
                .isHost(false)
                .approval(false)
                .build());
    }

    /**
     * 프로그램 참여 승인
     */
    @Transactional
    public void approval(Long memberId, Long programId) {
        getParticipant(memberId, programId).approve();
    }

    /**
     * 프로그램 참여 거부
     */
    @Transactional
    public void reject(Long memberId, Long programId) {
        participantRepository.delete(getParticipant(memberId, programId));
    }


    private Participant getParticipant(Long memberId, Long programId) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(NoSuchElementException::new);
        Program findProgram = programRepository.findById(programId).orElseThrow(NoSuchElementException::new);
        return participantRepository.findByMemberAndProgram(findMember, findProgram).orElseThrow(NoSuchElementException::new);
    }

}
