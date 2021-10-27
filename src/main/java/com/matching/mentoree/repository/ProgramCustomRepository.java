package com.matching.mentoree.repository;

import com.matching.mentoree.domain.Category;
import com.matching.mentoree.domain.Member;
import com.matching.mentoree.service.dto.ProgramDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

import static com.matching.mentoree.service.dto.ProgramDTO.*;

public interface ProgramCustomRepository {

    Slice<ProgramInfoDTO> findRecommendPrograms(Member login, Pageable pageable);
    Slice<ProgramInfoDTO> findAllProgram(Member login, Pageable pageable);
    Optional<ProgramInfoDTO> findProgramById(Long programId);

}
