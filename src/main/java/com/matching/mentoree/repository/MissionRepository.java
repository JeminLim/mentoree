package com.matching.mentoree.repository;

import com.matching.mentoree.domain.Mission;
import com.matching.mentoree.service.dto.MissionDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MissionRepository extends JpaRepository<Mission, Long>, MissionCustomRepository {


}
