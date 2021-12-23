package com.mentoree.board.api;

import com.mentoree.member.domain.Member;
import com.mentoree.board.repository.BoardRepository;
import com.mentoree.member.repository.MemberRepository;
import com.mentoree.board.service.BoardService;
import com.mentoree.board.api.dto.BoardDTO.BoardInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BoardAPIController {

    private final BoardService boardService;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    @GetMapping("/board/{boardId}/info")
    public ResponseEntity getBoardInfo(@PathVariable("boardId") long boardId) {
        BoardInfo boardInfo = boardRepository.findBoardInfoById(boardId).orElseThrow(NoSuchElementException::new);
        Map<String, Object> data = new HashMap<>();
        data.put("boardInfo", boardInfo);
        return ResponseEntity.ok().body(data);
    }

    @PostMapping("/board")
    public ResponseEntity createBoard(@RequestBody BoardInfo createRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) authentication.getPrincipal();
        Member loginUser = memberRepository.findByEmail(email).orElseThrow(NoSuchElementException::new);
        boardService.saveBoard(createRequest, loginUser);
        return ResponseEntity.ok().body("success");
    }

}
