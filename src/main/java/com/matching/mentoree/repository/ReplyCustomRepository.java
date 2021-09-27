package com.matching.mentoree.repository;

import com.matching.mentoree.domain.Board;
import com.matching.mentoree.service.dto.ReplyDTO;

import java.util.List;

public interface ReplyCustomRepository {

    List<ReplyDTO> findRepliesAllByBoard(Long boardId);

}
