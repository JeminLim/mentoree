package com.matching.mentoree.repository;

import com.matching.mentoree.domain.Category;
import com.matching.mentoree.domain.Member;
import com.matching.mentoree.service.dto.ProgramDTO;

import java.util.List;
import java.util.Optional;

import static com.matching.mentoree.service.dto.ProgramDTO.*;

public interface ProgramCustomRepository {

    List<ProgramInfoDTO> findRecommendPrograms(Member login);
    List<ProgramInfoDTO> findAllProgram();
    Optional<ProgramInfoDTO> findProgramById(Long programId);

}
