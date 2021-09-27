package com.matching.mentoree.repository;


import com.matching.mentoree.domain.*;
import com.matching.mentoree.service.dto.ProgramDTO;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.matching.mentoree.domain.QMember.*;
import static com.matching.mentoree.domain.QParticipant.*;
import static com.matching.mentoree.domain.QProgram.*;
import static com.matching.mentoree.service.dto.ProgramDTO.*;

public class ParticipantCustomRepositoryImpl implements ParticipantCustomRepository{

    private final JPAQueryFactory queryFactory;

    public ParticipantCustomRepositoryImpl(EntityManager em) { this.queryFactory = new JPAQueryFactory(em);}

    @Override
    public void rejectAll() {
        queryFactory.delete(participant)
                .where(participant.approval.isFalse())
                .execute();
    }

    @Override
    public List<Participant> findParticipateHistory(Member targetMember) {
        return queryFactory.selectFrom(participant)
                .join(participant.program, program).fetchJoin()
                .where(participant.member.eq(targetMember))
                .fetch();
    }

    @Override
    public List<Participant> findAllApplicants(Program program) {
        return queryFactory.selectFrom(participant)
                .where(participant.program.eq(program),
                        participant.approval.eq(false))
                .fetch();
    }

    @Override
    public Optional<Participant> findParticipantByEmailAndProgram(String email, Long id) {
        return Optional.of(
                queryFactory.selectFrom(participant)
                .join(participant.member, member)
                .join(participant.program, program)
                .fetchJoin()
                .where(participant.member.email.eq(email),
                        participant.program.id.eq(id))
                .fetchOne()
        );
    }

    @Override
    public List<Participant> findMentor(Long programId) {
        return queryFactory.selectFrom(participant)
                .join(participant.program, program)
                .join(participant.member, member)
                .fetchJoin()
                .where(participant.program.id.eq(programId))
                .fetch();
    }

    @Override
    public List<ProgramForNavbarDTO> findParticipateProgram(String email) {
        return queryFactory.select(Projections.bean(ProgramForNavbarDTO.class,
                participant.program.id.as("id"),
                participant.program.programName.as("title")))
                .from(participant)
                .join(participant.program, program)
                .join(participant.member, member)
                .where(participant.member.email.eq(email))
                .fetch();
    }

    @Override
    public Optional<Participant> findHost(Long programId) {
        return Optional.of(
                queryFactory.selectFrom(participant)
                .join(participant.program, program)
                .join(participant.member, member)
                .fetchJoin()
                .where(participant.isHost.eq(true)
                        .and(participant.program.id.eq(programId)))
                .fetchOne()
        );
    }


}
