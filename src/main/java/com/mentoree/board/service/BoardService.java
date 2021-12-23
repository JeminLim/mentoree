package com.mentoree.board.service;

import com.mentoree.board.domain.Board;
import com.mentoree.member.domain.Member;
import com.mentoree.mission.domain.Mission;
import com.mentoree.board.repository.BoardRepository;
import com.mentoree.mission.repository.MissionRepository;
import com.mentoree.board.api.dto.BoardDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final MissionRepository missionRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public void saveBoard(BoardDTO.BoardInfo dto, Member login) {
        Mission mission = missionRepository.findById(dto.getMissionId()).orElseThrow(NoSuchElementException::new);
        boardRepository.save(dto.toEntity(mission, login));
    }

    @Transactional
    public void updateBoard(BoardDTO.BoardInfo dto, Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(NoSuchElementException::new);
        if(dto.getContent() != null && !dto.getContent().equals(board.getContent()))
            board.updateContent(dto.getContent());
    }

    @Transactional
    public void deleteMenteeBoard(Long menteeBoardId) {
        boardRepository.deleteById(menteeBoardId);
    }


}
