package com.matching.mentoree.repository;


import com.matching.mentoree.domain.QParticipant;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

import static com.matching.mentoree.domain.QParticipant.*;

public class ParticipantCustomRepositoryImpl implements ParticipantCustomRepository{

    private final JPAQueryFactory queryFactory;

    public ParticipantCustomRepositoryImpl(EntityManager em) { this.queryFactory = new JPAQueryFactory(em);}

    @Override
    public void rejectAll() {
        queryFactory.delete(participant)
                .where(participant.approval.isFalse())
                .execute();
    }
}
