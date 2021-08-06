package com.matching.mentoree.service;

import com.matching.mentoree.domain.Member;
import com.matching.mentoree.domain.MenteeBoard;
import com.matching.mentoree.domain.MentorBoard;
import com.matching.mentoree.domain.Mission;
import com.matching.mentoree.repository.MenteeBoardRepository;
import com.matching.mentoree.repository.MentorBoardRepository;
import com.matching.mentoree.repository.MissionRepository;
import com.matching.mentoree.service.dto.BoardDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final MissionRepository missionRepository;
    private final MenteeBoardRepository menteeBoardRepository;
    private final MentorBoardRepository mentorBoardRepository;

    @Transactional
    public void saveMenteeBoard(BoardDTO dto, Member login) {
        Mission mission = missionRepository.findById(dto.getMissionId()).orElseThrow(NoSuchElementException::new);
        menteeBoardRepository.save(dto.toMenteeBoardEntity(mission, login));
    }

    @Transactional
    public void saveMentorBoard(BoardDTO dto, Member login) {
        MenteeBoard menteeBoard = menteeBoardRepository.findById(dto.getMenteeBoardId()).orElseThrow(NoSuchElementException::new);
        mentorBoardRepository.save(dto.toMentorBoardEntity(menteeBoard, login));
    }

    @Transactional
    public void updateMenteeBoard(BoardDTO dto, Long boardId) {
        MenteeBoard menteeBoard = menteeBoardRepository.findById(boardId).orElseThrow(NoSuchElementException::new);
        if(dto.getContent() != null && !dto.getContent().equals(menteeBoard.getContent()))
            menteeBoard.updateContent(dto.getContent());
    }

    @Transactional
    public void updateMentorBoard(BoardDTO dto, Long boardId) {
        MentorBoard mentorBoard = mentorBoardRepository.findById(boardId).orElseThrow(NoSuchElementException::new);
        if(dto.getContent() != null && !dto.getContent().equals(mentorBoard.getFeedback()))
            mentorBoard.updateFeedback(dto.getContent());
    }

    @Transactional
    public void deleteMenteeBoard(Long menteeBoardId) {
        menteeBoardRepository.deleteById(menteeBoardId);
    }

    @Transactional
    public void deleteMentorBoard(Long mentorBoardId) {
        mentorBoardRepository.deleteById(mentorBoardId);
    }


}
