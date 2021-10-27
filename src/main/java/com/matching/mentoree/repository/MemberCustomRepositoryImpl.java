package com.matching.mentoree.repository;

import com.matching.mentoree.domain.*;
import com.matching.mentoree.service.dto.MemberDTO;
import com.matching.mentoree.service.dto.MemberDTO.MemberInfo;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.matching.mentoree.domain.QCategory.*;
import static com.matching.mentoree.domain.QMember.*;
import static com.matching.mentoree.domain.QMemberInterest.*;
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
                member.link
                )).from(member)
                .where(member.email.eq(email))
                .fetchOne();

        List<String> categories = queryFactory.select(memberInterest.category.categoryName)
                .from(memberInterest)
                .join(memberInterest.category, category)
                .join(memberInterest.member, member)
                .where(memberInterest.member.email.eq(email))
                .fetch();

        if(categories != null) memberInfo.setCategories(categories);
        return Optional.of(memberInfo);
    }


}
