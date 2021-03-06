package com.mentoree.program.repository;

import com.mentoree.global.repository.util.RepositoryHelper;
import com.mentoree.program.domain.ProgramRole;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
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

import static com.mentoree.category.domain.QCategory.*;
import static com.mentoree.member.domain.QMember.*;
import static com.mentoree.participants.api.dto.ParticipantDTOCollection.*;
import static com.mentoree.participants.domain.QParticipant.*;
import static com.mentoree.program.domain.QProgram.*;
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
                .where( notInParticipatedPrograms(participatedPrograms),
                        inInterestCategory(interests),
                        program.isOpen.eq(true))
                .limit(pageable.getPageSize() + 1)
                .offset(pageable.getOffset())
                .fetch();
        Map<Long, List<ParticipantDTO>> mentorInProgram = findMentorInProgram(toProgramIds(queryResults));
        if(mentorInProgram != null)
            queryResults.forEach(o -> o.setMentor(mentorInProgram.get(o.getId())));

        return RepositoryHelper.toSlice(queryResults, pageable);
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
                .where( notInParticipatedPrograms(participatedPrograms),
                        program.isOpen.eq(true))
                .limit(pageable.getPageSize() + 1)
                .offset(pageable.getOffset())
                .fetch();

        Map<Long, List<ParticipantDTO>> mentorInProgram = findMentorInProgram(toProgramIds(result));
        if(mentorInProgram != null)
            result.forEach(o -> o.setMentor(mentorInProgram.get(o.getId())));

        return RepositoryHelper.toSlice(result, pageable);
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

    @Override
    public boolean existsByTitle(String title) {
        return queryFactory.selectFrom(program)
                .where(program.programName.eq(title))
                .fetchCount() > 0;
    }

    private BooleanExpression notInParticipatedPrograms(List<Long> participatedPrograms) {
        return participatedPrograms != null ? program.id.notIn(participatedPrograms) : null;
    }

    private BooleanExpression inInterestCategory(List<String> interests) {
        return interests != null ? program.category.categoryName.in(interests) : null;
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
