package com.matching.mentoree.api;

import com.matching.mentoree.domain.Board;
import com.matching.mentoree.domain.Member;
import com.matching.mentoree.repository.BoardRepository;
import com.matching.mentoree.repository.MemberRepository;
import com.matching.mentoree.repository.ReplyRepository;
import com.matching.mentoree.service.dto.ReplyDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
public class ReplyAPIController {

    private final ReplyRepository replyRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    @GetMapping("/program/{programId}/mission/{missionId}/board/{boardId}/reply/list")
    public List<ReplyDTO> getReplies(@PathVariable("boardId") long boardId) {
        Board findBoard = boardRepository.findById(boardId).orElseThrow(NoSuchElementException::new);
        List<ReplyDTO> allByBoard = replyRepository.findRepliesAllByBoard(boardId);
        return allByBoard;
    }

}
