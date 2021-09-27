package com.matching.mentoree.repository;

import com.matching.mentoree.domain.Program;
import com.matching.mentoree.service.dto.ProgramDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import static com.matching.mentoree.service.dto.ProgramDTO.*;

public interface ProgramRepository extends JpaRepository<Program, Long>, ProgramCustomRepository {

}
