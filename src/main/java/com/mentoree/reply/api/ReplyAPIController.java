package com.mentoree.reply.api;

import com.mentoree.board.domain.Board;
import com.mentoree.member.domain.Member;
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
@RequestMapping("/api/programs")
public class ReplyAPIController {

    private final ReplyRepository replyRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    @ApiOperation(value = "댓글 리스트 요청",notes = "게시글 소속 댓글 리스트 반환")
    @GetMapping("/{programId}/missions/{missionId}/boards/{boardId}/replies")
    public ResponseEntity getReplies(@ApiParam(value = "해당 프로그램 ID", required = true) @PathVariable("programId") long programId,
                                     @ApiParam(value = "해당 미션 ID", required = true) @PathVariable("missionId") long missionId,
                                     @ApiParam(value = "요청 게시글 ID", required = true) @PathVariable("boardId") long boardId) {
        List<ReplyDTO> reply = replyRepository.findRepliesAllByBoard(boardId);
        return ResponseEntity.ok().body(reply);
    }

    //== 댓글 작성 관련 ==//
    @ApiOperation(value = "댓글 작성 요청",notes = "댓글 작성 결과 반환")
    @PostMapping("/{programId}/missions/{missionId}/boards/{boardId}/replies/new")
    public ResponseEntity replyWrite(@ApiParam(value = "해당 프로그램 ID", required = true) @PathVariable("programId") long programId,
                                     @ApiParam(value = "해당 미션 ID", required = true) @PathVariable("missionId") long missionId,
                                     @ApiParam(value = "요청 게시글 ID", required = true) @PathVariable("boardId") long boardId,
                                     @ApiParam(value = "댓글 작성 폼", required = true) @Validated @RequestBody ReplyDTO replyCreateForm,
                                     BindingResult bindingResult) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loginEmail = (String) auth.getPrincipal();
        Member login = memberRepository.findByEmail(loginEmail).orElseThrow(NoSuchElementException::new);
        Board target = boardRepository.findById(replyCreateForm.getBoardId()).orElseThrow(NoSuchElementException::new);
        Reply savedReply = replyRepository.save(replyCreateForm.toEntity(target, login));
        ReplyDTO result = ReplyDTO.of(savedReply);
        return ResponseEntity.ok().body(result);
    }

}
