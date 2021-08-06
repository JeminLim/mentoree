package com.matching.mentoree.repository;

import com.matching.mentoree.domain.Member;
import com.matching.mentoree.domain.MenteeBoard;
import com.matching.mentoree.domain.Mission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MenteeBoardRepository extends JpaRepository<MenteeBoard, Long> {

    Optional<MenteeBoard> findByMissionAndWriter(Mission mission, Member writer);

}
