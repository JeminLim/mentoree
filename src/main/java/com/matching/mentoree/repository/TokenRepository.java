package com.matching.mentoree.repository;

import com.matching.mentoree.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByEmail(String email);
}
