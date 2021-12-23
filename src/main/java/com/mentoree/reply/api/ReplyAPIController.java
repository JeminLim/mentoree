package com.mentoree.reply.api;

import com.mentoree.board.domain.Board;
import com.mentoree.member.domain.Member;
import com.mentoree.reply.domain.Reply;
import com.mentoree.board.repository.BoardRepository;
import com.mentoree.member.repository.MemberRepository;
import com.mentoree.global.repository.ReplyRepository;
import com.mentoree.reply.api.dto.ReplyDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReplyAPIController {

    private final ReplyRepository replyRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    @GetMapping("/board/{boardId}/reply")
    public ResponseEntity getReplies(@PathVariable("boardId") long boardId) {
        List<ReplyDTO> reply = replyRepository.findRepliesAllByBoard(boardId);
        return ResponseEntity.ok().body(reply);
    }

    //== 댓글 작성 관련 ==//
    @PostMapping("/reply")
    public ResponseEntity replyWrite(@RequestBody ReplyDTO replyCreateForm) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loginEmail = (String) auth.getPrincipal();
        Member login = memberRepository.findByEmail(loginEmail).orElseThrow(NoSuchElementException::new);
        Board target = boardRepository.findById(replyCreateForm.getBoardId()).orElseThrow(NoSuchElementException::new);
        Reply savedReply = replyRepository.save(replyCreateForm.toEntity(target, login));
        ReplyDTO result = ReplyDTO.of(savedReply);
        return ResponseEntity.ok().body(result);
    }

}
