package com.matching.mentoree.repository;

import com.matching.mentoree.domain.*;
import com.matching.mentoree.repository.util.RepositoryHelper;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.matching.mentoree.domain.QCategory.*;
import static com.matching.mentoree.domain.QMember.*;
import static com.matching.mentoree.domain.QMemberInterest.*;
import static com.matching.mentoree.domain.QParticipant.*;
import static com.matching.mentoree.domain.QProgram.*;
import static com.matching.mentoree.service.dto.ProgramDTO.*;

@Slf4j
public class ProgramCustomRepositoryImpl implements ProgramCustomRepository{

    private final JPAQueryFactory queryFactory;

    public ProgramCustomRepositoryImpl(EntityManager em) { this.queryFactory = new JPAQueryFactory(em);}

    @Override
    public Slice<ProgramInfoDTO> findRecommendPrograms(Member login, Pageable pageable) {
        List<Long> participatedProgramIds = findParticipatedProgramIds(login);
        List<ProgramInfoDTO> queryResults = queryFactory.select(Projections.fields(ProgramInfoDTO.class,
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
                .where( program.id.notIn(participatedProgramIds),
                        program.category.in(JPAExpressions.select(memberInterest.category)
                                                                .from(memberInterest)
                                                                .where(memberInterest.member.eq(login)))
                        ,program.isOpen.eq(true))
                .limit(pageable.getPageSize() + 1)
                .offset(pageable.getOffset())
                .fetch();
        Map<Long, List<Participant>> mentorInProgram = findMentorInProgram(toProgramIds(queryResults));
        if(mentorInProgram != null)
            queryResults.forEach(o -> o.setMentor(mentorInProgram.get(o.getId())));

        Slice<ProgramInfoDTO> recommendPrograms = RepositoryHelper.toSlice(queryResults, pageable);
        return recommendPrograms;
    }

    @Override
    public Slice<ProgramInfoDTO> findAllProgram(Member login, Pageable pageable) {
        List<Long> participatedProgramIds = findParticipatedProgramIds(login);
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
                .where( program.id.notIn(participatedProgramIds),
                        program.isOpen.eq(true))
                .limit(pageable.getPageSize() + 1)
                .offset(pageable.getOffset())
                .fetch();

        Map<Long, List<Participant>> mentorInProgram = findMentorInProgram(toProgramIds(result));
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

        Map<Long, List<Participant>> mentorInProgram = findMentorInProgram(id);
        if(mentorInProgram != null)
            programInfoDTO.setMentor(mentorInProgram.get(id.get(0)));

        return Optional.of(programInfoDTO);
    }

    private List<Long> toProgramIds(List<ProgramInfoDTO> result) {
        return result.stream()
                .map(o -> o.getId())
                .collect(Collectors.toList());
    }

    private Map<Long, List<Participant>> findMentorInProgram(List<Long> programIds) {
        List<Participant> participants = queryFactory.selectFrom(participant)
                .join(participant.program, program)
                .join(participant.member, member)
                .fetchJoin()
                .where(participant.program.id.in(programIds).and(participant.role.eq(ProgramRole.MENTOR)))
                .fetch();

        return participants.size() > 0 ? participants.stream().collect(Collectors.groupingBy(p -> p.getProgram().getId())) : null;
    }

    private List<Long> findParticipatedProgramIds(Member loginMember) {
        return queryFactory.select(participant.program.id)
                .from(participant)
                .join(participant.program, program)
                .where(participant.member.eq(loginMember))
                .fetch();
    }

}
