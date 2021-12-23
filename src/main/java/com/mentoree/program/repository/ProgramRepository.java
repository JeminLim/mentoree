package com.mentoree.program.repository;

import com.mentoree.program.domain.Program;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgramRepository extends JpaRepository<Program, Long>, ProgramCustomRepository {

}
