package com.matching.mentoree.service;

import com.matching.mentoree.domain.Board;
import com.matching.mentoree.domain.Member;
import com.matching.mentoree.domain.Mission;
import com.matching.mentoree.repository.BoardRepository;
import com.matching.mentoree.repository.MissionRepository;
import com.matching.mentoree.service.dto.BoardDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static com.matching.mentoree.service.dto.BoardDTO.*;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final MissionRepository missionRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public void saveBoard(BoardInfo dto, Member login) {
        Mission mission = missionRepository.findById(dto.getMissionId()).orElseThrow(NoSuchElementException::new);
        boardRepository.save(dto.toEntity(mission, login));
    }

    @Transactional
    public void updateMenteeBoard(BoardInfo dto, Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(NoSuchElementException::new);
        if(dto.getContent() != null && !dto.getContent().equals(board.getContent()))
            board.updateContent(dto.getContent());
    }

    @Transactional
    public void deleteMenteeBoard(Long menteeBoardId) {
        boardRepository.deleteById(menteeBoardId);
    }


}
