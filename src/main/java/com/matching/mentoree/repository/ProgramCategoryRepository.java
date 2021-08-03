package com.matching.mentoree.repository;

import com.matching.mentoree.domain.ProgramCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgramCategoryRepository extends JpaRepository<ProgramCategory, Long> {
}
