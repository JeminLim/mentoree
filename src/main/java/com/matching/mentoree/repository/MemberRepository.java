package com.matching.mentoree.repository;

import com.matching.mentoree.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberCustomRepository {
    boolean existsByEmail(String email);
    Optional<Member> findByEmail(String email);
}
