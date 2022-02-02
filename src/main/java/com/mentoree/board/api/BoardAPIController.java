package com.mentoree.board.api;

import com.mentoree.member.domain.Member;
import com.mentoree.board.repository.BoardRepository;
import com.mentoree.member.repository.MemberRepository;
import com.mentoree.board.service.BoardService;
import com.mentoree.board.api.dto.BoardDTO.BoardInfo;
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
@RequestMapping("/api/programs/")
@Api("Board Controller API")
public class BoardAPIController {

    private final BoardService boardService;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    @ApiOperation(value = "게시글 내용 요청",notes = "게시글 내용 반환")
    @GetMapping("/{programId}/missions/{missionId}/boards/{boardId}")
    public ResponseEntity getBoardInfo(@ApiParam(value = "해당 프로그램 ID", required = true) @PathVariable("programId") long programId,
                                       @ApiParam(value = "해당 미션 ID", required = true) @PathVariable("missionId") long missionId,
                                       @ApiParam(value = "요청 게시글 ID", required = true) @PathVariable("boardId") long boardId) {
        BoardInfo boardInfo = boardRepository.findBoardInfoById(boardId).orElseThrow(NoSuchElementException::new);
        Map<String, Object> data = new HashMap<>();
        data.put("boardInfo", boardInfo);
        return ResponseEntity.ok().body(data);
    }

    @ApiOperation(value = "게시글 작성 요청",notes = "게시글 작성 요청 결과 반환")
    @PostMapping("/{programId}/missions/{missionId}/boards/new")
    public ResponseEntity createBoard(@ApiParam(value = "해당 프로그램 ID", required = true) @PathVariable("programId") long programId,
                                      @ApiParam(value = "해당 미션 ID", required = true) @PathVariable("missionId") long missionId,
                                      @ApiParam(value = "게시글 작성 폼", required = true) @Validated @RequestBody BoardInfo createRequest,
                                      BindingResult bindingResult) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) authentication.getPrincipal();
        Member loginUser = memberRepository.findByEmail(email).orElseThrow(NoSuchElementException::new);
        boardService.saveBoard(createRequest, loginUser);
        return ResponseEntity.ok().body("success");
    }

}
