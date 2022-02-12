package com.mentoree.program.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

import static com.mentoree.program.api.dto.ProgramDTO.*;

public interface ProgramCustomRepository {

    Slice<ProgramInfoDTO> findRecommendPrograms(List<Long> participatedPrograms, List<String> interests, Pageable pageable);
    Slice<ProgramInfoDTO> findAllProgram(List<Long> participatedPrograms, Pageable pageable);
    Optional<ProgramInfoDTO> findProgramById(Long programId);
    boolean existsByTitle(String title);

}
