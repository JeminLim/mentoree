package com.matching.mentoree.service;

import com.matching.mentoree.domain.*;
import com.matching.mentoree.repository.*;
import com.matching.mentoree.service.dto.ProgramCreateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgramService {

    private final ProgramRepository programRepository;
    private final CategoryRepository categoryRepository;
    private final ProgramCategoryRepository programCategoryRepository;
    private final MemberRepository memberRepository;
    private final ParticipantRepository participantRepository;
    private final EntityManager em;

    @Transactional
    public void createProgram(ProgramCreateDTO createDTO, Long loginMemberId) {

        Program program = programRepository.save(createDTO.toProgramEntity());

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

        Member findMember = memberRepository.findById(loginMemberId).orElseThrow(NoSuchElementException::new);
        Participant participant = Participant.builder()
                                    .program(program)
                                    .member(findMember)
                                    .isHost(true)
                                    .role(ProgramRole.valueOf(createDTO.getProgramRole()))
                                    .approval(true)
                                    .build();
        participantRepository.save(participant);
    }




}
