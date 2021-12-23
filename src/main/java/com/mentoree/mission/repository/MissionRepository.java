package com.mentoree.mission.repository;

import com.mentoree.mission.domain.Mission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionRepository extends JpaRepository<Mission, Long>, MissionCustomRepository {


}
