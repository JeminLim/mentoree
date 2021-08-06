package com.matching.mentoree.repository;


import com.matching.mentoree.domain.Member;
import com.matching.mentoree.domain.Participant;
import com.matching.mentoree.domain.QParticipant;
import com.matching.mentoree.domain.QProgram;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

import java.util.List;

import static com.matching.mentoree.domain.QParticipant.*;
import static com.matching.mentoree.domain.QProgram.*;

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


}
