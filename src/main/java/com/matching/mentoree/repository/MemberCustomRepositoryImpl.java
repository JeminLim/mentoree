package com.matching.mentoree.repository;

import com.matching.mentoree.domain.*;
import com.matching.mentoree.service.dto.MemberDTO;
import com.matching.mentoree.service.dto.MemberDTO.MemberInfo;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.matching.mentoree.domain.QMember.*;
import static com.matching.mentoree.domain.QParticipant.participant;
import static com.matching.mentoree.domain.QProgram.program;

public class MemberCustomRepositoryImpl implements MemberCustomRepository{

    private final JPAQueryFactory queryFactory;

    public MemberCustomRepositoryImpl(EntityManager em) { this.queryFactory = new JPAQueryFactory(em);}


    @Override
    public Optional<MemberInfo> findMemberInfoByEmail(String email) {
        MemberInfo memberInfo = queryFactory.select(Projections.bean(MemberInfo.class,
                member.id,
                member.email,
                member.memberName,
                member.nickname,
                member.thumbnailImgUrl.as("imgUrl"),
                member.link
        ))
                .from(member)
                .where(member.email.eq(email))
                .fetchOne();

        List<Program> participatedProgram = findParticipatedProgram(memberInfo.getId());
        memberInfo.setProgramList(participatedProgram);
        return Optional.of(memberInfo);
    }

    private List<Program> findParticipatedProgram(Long memberId) {
        return queryFactory.select(participant.program)
                .from(participant)
                .join(participant.program, program)
                .join(participant.member, member)
                .where(participant.member.id.eq(memberId)).fetch();
    }


}
