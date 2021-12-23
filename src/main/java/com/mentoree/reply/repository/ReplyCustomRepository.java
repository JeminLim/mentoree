package com.mentoree.reply.repository;

import com.mentoree.reply.api.dto.ReplyDTO;

import java.util.List;

public interface ReplyCustomRepository {

    List<ReplyDTO> findRepliesAllByBoard(Long boardId);

}
