package com.mentoree.reply.api;

import com.mentoree.board.domain.Board;
import com.mentoree.config.security.UserPrincipal;
import com.mentoree.global.exception.BindingFailureException;
import com.mentoree.global.exception.NoAuthorityException;
import com.mentoree.member.domain.Member;
import com.mentoree.participants.repository.ParticipantRepository;
import com.mentoree.reply.domain.Reply;
import com.mentoree.board.repository.BoardRepository;
import com.mentoree.member.repository.MemberRepository;
import com.mentoree.global.repository.ReplyRepository;
import com.mentoree.reply.api.dto.ReplyDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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
    private final ParticipantRepository participantRepository;

    @ApiOperation(value = "댓글 리스트 요청",notes = "게시글 소속 댓글 리스트 반환")
    @GetMapping("/replies/list")
    public ResponseEntity getReplies(@ApiParam(value = "요청 게시글 ID", required = true) @RequestParam("boardId") long boardId) {
        List<ReplyDTO> reply = replyRepository.findRepliesAllByBoard(boardId);
        return ResponseEntity.ok().body(reply);
    }

    //== 댓글 작성 관련 ==//
    @ApiOperation(value = "댓글 작성 요청", notes = "댓글 작성 결과 반환")
    @PostMapping("/replies/new")
    public ResponseEntity replyWrite(@ApiParam(value = "댓글 작성 폼", required = true) @Validated @RequestBody ReplyDTO replyCreateForm,
                                     @ApiParam(hidden = true) BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            throw new BindingFailureException(bindingResult, "잘못된 댓글 작성 요청입니다.");
        }
        String loginEmail = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();

        if(!participantRepository.isParticipantByEmailAndBoardId(loginEmail, replyCreateForm.getBoardId())) {
            throw new NoAuthorityException("참가자가 아닙니다");
        }

        Member login = memberRepository.findByEmail(loginEmail).orElseThrow(NoSuchElementException::new);
        Board target = boardRepository.findById(replyCreateForm.getBoardId()).orElseThrow(NoSuchElementException::new);
        Reply savedReply = replyRepository.save(replyCreateForm.toEntity(target, login));
        ReplyDTO result = ReplyDTO.of(savedReply);
        return ResponseEntity.ok().body(result);
    }

}
