package com.mentoree.program.repository;

import com.mentoree.global.repository.util.RepositoryHelper;
import com.mentoree.participants.api.dto.ParticipantDTO;
import com.mentoree.member.domain.Member;
import com.mentoree.program.domain.ProgramRole;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mentoree.domain.QCategory.*;
import static com.mentoree.domain.QMember.*;
import static com.mentoree.domain.QParticipant.*;
import static com.mentoree.domain.QProgram.*;
import static com.mentoree.program.api.dto.ProgramDTO.*;

@Slf4j
public class ProgramCustomRepositoryImpl implements ProgramCustomRepository{

    private final JPAQueryFactory queryFactory;

    public ProgramCustomRepositoryImpl(EntityManager em) { this.queryFactory = new JPAQueryFactory(em);}

    @Override
    public Slice<ProgramInfoDTO> findRecommendPrograms(List<Long> participatedPrograms, List<String> interests, Pageable pageable) {
        List<ProgramInfoDTO> queryResults = queryFactory.select(Projections.bean(ProgramInfoDTO.class,
                program.id,
                program.programName.as("title"),
                program.category.categoryName.as("category"),
                program.maxMember,
                program.goal,
                program.description,
                program.dueDate))
                .from(program)
                .join(program.category, category)
                .join(program.participants, participant)
                .where( program.id.notIn(participatedPrograms),
                        program.category.categoryName.in(interests)
                        ,program.isOpen.eq(true))
                .limit(pageable.getPageSize() + 1)
                .offset(pageable.getOffset())
                .fetch();
        Map<Long, List<ParticipantDTO>> mentorInProgram = findMentorInProgram(toProgramIds(queryResults));
        if(mentorInProgram != null)
            queryResults.forEach(o -> o.setMentor(mentorInProgram.get(o.getId())));

        Slice<ProgramInfoDTO> recommendPrograms = RepositoryHelper.toSlice(queryResults, pageable);
        return recommendPrograms;
    }

    @Override
    public Slice<ProgramInfoDTO> findAllProgram(List<Long> participatedPrograms, Pageable pageable) {
        List<ProgramInfoDTO> result = queryFactory.select(Projections.bean(ProgramInfoDTO.class,
                program.id,
                program.programName.as("title"),
                program.category.categoryName.as("category"),
                program.maxMember,
                program.goal,
                program.description,
                program.dueDate))
                .from(program)
                .join(program.category, category)
                .join(program.participants, participant)
                .where( program.id.notIn(participatedPrograms),
                        program.isOpen.eq(true))
                .limit(pageable.getPageSize() + 1)
                .offset(pageable.getOffset())
                .fetch();

        Map<Long, List<ParticipantDTO>> mentorInProgram = findMentorInProgram(toProgramIds(result));
        if(mentorInProgram != null)
            result.forEach(o -> o.setMentor(mentorInProgram.get(o.getId())));

        Slice<ProgramInfoDTO> allPrograms = RepositoryHelper.toSlice(result, pageable);
        return allPrograms;
    }

    @Override
    public Optional<ProgramInfoDTO> findProgramById(Long programId) {
        ProgramInfoDTO programInfoDTO = queryFactory.select(Projections.bean(ProgramInfoDTO.class,
                program.id,
                program.programName.as("title"),
                program.category.categoryName.as("category"),
                program.maxMember,
                program.goal,
                program.description,
                program.dueDate))
                .from(program)
                .join(program.category, category)
                .where(program.id.eq(programId)).fetchOne();

        List<Long> id = new ArrayList<>();
        id.add(programId);

        Map<Long, List<ParticipantDTO>> mentorInProgram = findMentorInProgram(id);
        if(mentorInProgram != null) {
            programInfoDTO.setMentor(mentorInProgram.get(programInfoDTO.getId()));
        }

        return Optional.ofNullable(programInfoDTO);
    }

    private List<Long> toProgramIds(List<ProgramInfoDTO> result) {
        return result.stream()
                .map(ProgramInfoDTO::getId)
                .collect(Collectors.toList());
    }

    private Map<Long, List<ParticipantDTO>> findMentorInProgram(List<Long> programIds) {
        List<ParticipantDTO> participants = queryFactory.select(Projections.bean(ParticipantDTO.class,
                    participant.member.id.as("id"),
                    program.id.as("programId"),
                    participant.member.email,
                    participant.member.nickname,
                    participant.role,
                    participant.isHost
                ))
                .from(participant)
                .join(participant.program, program)
                .join(participant.member, member)
                .where(participant.program.id.in(programIds),
                        participant.role.eq(ProgramRole.MENTOR))
                .fetch();

        return participants.size() > 0 ? participants.stream().collect(Collectors.groupingBy(ParticipantDTO::getProgramId)) : null;
    }
}
