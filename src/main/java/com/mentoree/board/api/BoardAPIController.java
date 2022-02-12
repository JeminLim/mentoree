package com.mentoree.board.api;

import com.mentoree.global.exception.BindingFailureException;
import com.mentoree.global.exception.NoAuthorityException;
import com.mentoree.member.domain.Member;
import com.mentoree.board.repository.BoardRepository;
import com.mentoree.member.repository.MemberRepository;
import com.mentoree.board.service.BoardService;
import com.mentoree.board.api.dto.BoardDTO.BoardInfo;
import com.mentoree.participants.repository.ParticipantRepository;
import io.swagger.annotations.Api;
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

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Api("Board Controller API")
public class BoardAPIController {

    private final BoardService boardService;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final ParticipantRepository participantRepository;

    @ApiOperation(value = "게시글 내용 요청",notes = "게시글 내용 반환")
    @GetMapping("/boards/{boardId}")
    public ResponseEntity getBoardInfo(@ApiParam(value = "요청 게시글 ID", required = true) @PathVariable("boardId") long boardId) {
        BoardInfo boardInfo = boardRepository.findBoardInfoById(boardId).orElseThrow(NoSuchElementException::new);
        Map<String, Object> data = new HashMap<>();
        data.put("boardInfo", boardInfo);
        return ResponseEntity.ok().body(data);
    }

    @ApiOperation(value = "게시글 작성 요청",notes = "게시글 작성 요청 결과 반환")
    @PostMapping("/boards/new")
    public ResponseEntity createBoard(@ApiParam(value = "게시글 작성 폼", required = true) @Validated @RequestBody BoardInfo createRequest,
                                      @ApiParam(hidden = true) BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            throw new BindingFailureException(bindingResult, "잘못된 게시글 작성 요청입니다.");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) authentication.getPrincipal();

        if(!participantRepository.isParticipantByEmailAndMissionId(email, createRequest.getMissionId())) {
            throw new NoAuthorityException("참가자가 아닙니다.");
        }


        Member loginUser = memberRepository.findByEmail(email).orElseThrow(NoSuchElementException::new);
        boardService.saveBoard(createRequest, loginUser);
        return ResponseEntity.ok().body("success");
    }

}
