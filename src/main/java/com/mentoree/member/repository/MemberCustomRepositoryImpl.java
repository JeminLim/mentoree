package com.mentoree.member.repository;

import com.mentoree.member.api.dto.MemberDTO;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.mentoree.domain.QCategory.*;
import static com.mentoree.domain.QMember.*;
import static com.mentoree.domain.QMemberInterest.*;
import static com.mentoree.member.api.dto.MemberDTO.*;

public class MemberCustomRepositoryImpl implements MemberCustomRepository{

    private final JPAQueryFactory queryFactory;

    public MemberCustomRepositoryImpl(EntityManager em) { this.queryFactory = new JPAQueryFactory(em);}

    @Override
    public Optional<MemberInfo> findMemberInfoByEmail(String email) {
        MemberInfo memberInfo = queryFactory.select(Projections.bean(MemberInfo.class,
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
                .where(memberInterest.member.email.eq(email))
                .fetch();

        if(categories != null) memberInfo.setInterests(categories);
        return Optional.of(memberInfo);
    }



}
