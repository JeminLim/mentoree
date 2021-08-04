package com.matching.mentoree.repository;

import com.matching.mentoree.domain.Mission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionRepository extends JpaRepository<Mission, Long> {

}
