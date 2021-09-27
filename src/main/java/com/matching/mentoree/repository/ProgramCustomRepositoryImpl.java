package com.matching.mentoree.repository;

import com.matching.mentoree.domain.*;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.matching.mentoree.domain.QCategory.*;
import static com.matching.mentoree.domain.QMemberInterest.*;
import static com.matching.mentoree.domain.QParticipant.*;
import static com.matching.mentoree.domain.QProgram.*;
import static com.matching.mentoree.service.dto.ProgramDTO.*;

public class ProgramCustomRepositoryImpl implements ProgramCustomRepository{

    private final JPAQueryFactory queryFactory;

    public ProgramCustomRepositoryImpl(EntityManager em) { this.queryFactory = new JPAQueryFactory(em);}

    @Override
    public List<ProgramInfoDTO> findRecommendPrograms(Member login) {
        List<ProgramInfoDTO> recommendPrograms = queryFactory.select(Projections.fields(ProgramInfoDTO.class,
                program.programName.as("title"),
                program.category.categoryName.as("category"),
                program.endDate,
                program.maxMember,
                program.goal,
                program.description))
                .from(program)
                .join(program.category, category)
                .join(program.participants, participant)
                .where(program.category.in(
                        JPAExpressions.select(memberInterest.category)
                                .from(memberInterest)
                                .where(memberInterest.member.eq(login))
                )).fetch();

        Map<Long, List<Participant>> mentorInProgram = findMentorInProgram(toProgramIds(recommendPrograms));
        recommendPrograms.forEach(o -> o.setMentor(mentorInProgram.get(o.getId())));
        return recommendPrograms;
    }

    @Override
    public List<ProgramInfoDTO> findAllProgram() {
        List<ProgramInfoDTO> allPrograms = queryFactory.select(Projections.bean(ProgramInfoDTO.class,
                program.programName.as("title"),
                program.category.categoryName.as("category"),
                program.endDate,
                program.maxMember,
                program.goal,
                program.description))
                .from(program)
                .join(program.category, category)
                .join(program.participants, participant)
                .fetch();

        Map<Long, List<Participant>> mentorInProgram = findMentorInProgram(toProgramIds(allPrograms));
        allPrograms.forEach(o -> o.setMentor(mentorInProgram.get(o.getId())));
        return allPrograms;
    }

    @Override
    public Optional<ProgramInfoDTO> findProgramById(Long programId) {
        ProgramInfoDTO programInfoDTO = queryFactory.select(Projections.fields(ProgramInfoDTO.class,
                program.programName.as("title"),
                program.category.categoryName.as("category"),
                program.endDate,
                program.maxMember,
                program.goal,
                program.description))
                .from(program)
                .join(program.category, category)
                .join(program.participants, participant)
                .where(program.id.eq(programId)).fetchOne();

        List<Long> id = new ArrayList<>();
        id.add(programInfoDTO.getId());

        Map<Long, List<Participant>> mentorInProgram = findMentorInProgram(id);
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
                .where(participant.program.id.in(programIds).and(participant.role.eq(ProgramRole.MENTOR)))
                .fetch();

        return participants.stream()
                .collect(Collectors.groupingBy(Participant::getId));
    }

}
