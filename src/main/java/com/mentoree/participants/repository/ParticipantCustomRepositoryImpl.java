package com.mentoree.participants.repository;


import com.mentoree.participants.domain.Participant;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

import static com.mentoree.member.domain.QMember.*;
import static com.mentoree.participants.domain.QParticipant.*;
import static com.mentoree.program.domain.QProgram.*;
import static com.mentoree.participants.api.dto.ParticipantDTOCollection.*;
import static com.mentoree.program.api.dto.ProgramDTO.*;

public class ParticipantCustomRepositoryImpl implements ParticipantCustomRepository{

    private final JPAQueryFactory queryFactory;

    public ParticipantCustomRepositoryImpl(EntityManager em) { this.queryFactory = new JPAQueryFactory(em);}

    @Override
    public List<ApplyRequest> findAllApplicants(Long programId) {
        return queryFactory.select(Projections.bean(ApplyRequest.class,
                participant.member.id,
                participant.member.nickname,
                participant.program.id.as("programId"),
                participant.message,
                participant.role.as("role")))
                .from(participant)
                .join(participant.program, program)
                .join(participant.member, member)
                .where(participant.program.id.eq(programId),
                        participant.approval.eq(false))
                .fetch();
    }

    @Override
    public Optional<Participant> findParticipantByMemberIdAndProgram(Long memberId, Long programId) {
        return Optional.ofNullable(
                queryFactory.selectFrom(participant)
                .join(participant.member, member)
                .join(participant.program, program)
                .fetchJoin()
                .where(participant.member.id.eq(memberId),
                        participant.program.id.eq(programId),
                        participant.approval.eq(false))
                .fetchOne()
        );
    }

    @Override
    public List<ParticipatedProgramDTO> findParticipateProgram(String email) {
        return queryFactory.select(Projections.bean(ParticipatedProgramDTO.class,
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
        return Optional.ofNullable(
                queryFactory.selectFrom(participant)
                .join(participant.program, program)
                .join(participant.member, member)
                .fetchJoin()
                .where(participant.isHost.eq(true),
                        (participant.program.id.eq(programId)))
                .fetchOne()
        );
    }

    @Override
    public Long countCurrentMember(Long programId) {
        return queryFactory.selectFrom(participant)
                .from(participant)
                .where(participant.program.id.eq(programId),
                        participant.approval.eq(true))
                .fetchCount();
    }

    @Override
    public Boolean isApplicants(Long programId, Long memberId) {
        Participant applicant = queryFactory.selectFrom(participant)
                .from(participant)
                .where(participant.program.id.eq(programId),
                        participant.member.id.eq(memberId),
                        participant.approval.eq(false))
                .fetchOne();
        return applicant != null;
    }

}
