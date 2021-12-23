package com.mentoree.member.repository;

import com.mentoree.member.domain.Member;
import com.mentoree.member.domain.MemberInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberInterestRepository extends JpaRepository<MemberInterest, Long> {

    @Query("select m from MemberInterest m where m.member.id = :memberId")
    List<MemberInterest> findInterests(@Param("memberId") Long memberId);

}
