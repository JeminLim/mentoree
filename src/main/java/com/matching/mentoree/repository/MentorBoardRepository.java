package com.matching.mentoree.repository;

import com.matching.mentoree.domain.MentorBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentorBoardRepository extends JpaRepository<MentorBoard, Long> {

}
