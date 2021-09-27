package com.matching.mentoree.repository;

import com.matching.mentoree.service.dto.BoardDTO;

import java.util.List;
import java.util.Optional;

import static com.matching.mentoree.service.dto.BoardDTO.*;

public interface BoardCustomRepository {

    Optional<BoardInfo> findBoardInfoById(Long id);
    List<BoardInfo> findAllBoardInfoById(Long missionId);

}
