package com.mentoree.board.repository;

import com.mentoree.board.api.dto.BoardDTO;

import java.util.List;
import java.util.Optional;

public interface BoardCustomRepository {

    Optional<BoardDTO.BoardInfo> findBoardInfoById(Long id);
    List<BoardDTO.BoardInfo> findAllBoardInfoById(Long missionId);

}
