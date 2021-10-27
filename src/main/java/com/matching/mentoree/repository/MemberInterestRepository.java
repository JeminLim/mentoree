package com.matching.mentoree.repository;

import com.matching.mentoree.domain.Member;
import com.matching.mentoree.domain.MemberInterest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberInterestRepository extends JpaRepository<MemberInterest, Long> {
    List<MemberInterest> findByMember(Member member);
}
