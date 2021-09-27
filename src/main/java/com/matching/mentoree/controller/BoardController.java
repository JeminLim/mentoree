package com.matching.mentoree.controller;

import com.matching.mentoree.domain.Board;
import com.matching.mentoree.domain.Member;
import com.matching.mentoree.domain.Mission;
import com.matching.mentoree.repository.BoardRepository;
import com.matching.mentoree.repository.MemberRepository;
import com.matching.mentoree.repository.MissionRepository;
import com.matching.mentoree.repository.ReplyRepository;
import com.matching.mentoree.service.BoardService;
import com.matching.mentoree.service.dto.BoardDTO;
import com.matching.mentoree.service.dto.ReplyDTO;
import com.matching.mentoree.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.NoSuchElementException;

import static com.matching.mentoree.service.dto.BoardDTO.*;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final MemberRepository memberRepository;
    private final BoardService boardService;
    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;
    private final MissionRepository missionRepository;

    //== Board 작성 ==//
    @GetMapping("/board/write")
    public String createBoardGet(@RequestParam("missionId") long missionId, Model model) {
        BoardInfo boardDTO = new BoardInfo();
        boardDTO.setMissionId(missionId);
        Member login = memberRepository.findByEmail((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).orElseThrow(NoSuchElementException::new);
        boardDTO.setWriterNickname(login.getNickname());

        model.addAttribute("boardForm", boardDTO);

        return "boardCreate";
    }

    @PostMapping("/board/write")
    public String createBoardPost(@ModelAttribute("boardForm") BoardInfo createForm) {
        Member login = memberRepository.findByEmail((String)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).orElseThrow(NoSuchElementException::new);
        boardService.saveBoard(createForm, login);
        return "redirect:/mission/list";
    }

    //== 개별 Board 열람 ==//
    @GetMapping("/board/{boardId}")
    public String boardGet(@PathVariable("boardId") long boardId, Model model) {
        BoardInfo findBoard = boardRepository.findBoardInfoById(boardId).orElseThrow(NoSuchElementException::new);
        model.addAttribute("boardInfo", findBoard);

        List<ReplyDTO> allReplies = replyRepository.findRepliesAllByBoard(boardId);
        model.addAttribute("replyList", allReplies);

        ReplyDTO replyCreateForm = new ReplyDTO();
        replyCreateForm.setBoardId(boardId);
        model.addAttribute("replyCreateForm", replyCreateForm);
        return "boardInfo";
    }

    //== 전체 Board 열람 ==//
    @GetMapping("/{missionId}/board/list")
    public String boardListGet(@PathVariable("missionId") Long missionId, Model model) {
        Mission mission = missionRepository.findById(missionId).orElseThrow(NoSuchElementException::new);
        model.addAttribute("mission", mission);
        List<BoardInfo> boards = boardRepository.findAllBoardInfoById(missionId);
        model.addAttribute("boardList", boards);
        return "board";
    }

    //== 댓글 작성 관련 ==//
    @PostMapping("/reply")
    public String replyWrite(@ModelAttribute("replyCreateForm") ReplyDTO replyCreateForm) {
        String loginEmail = CommonUtil.getLoginEmail();
        Member login = memberRepository.findByEmail(loginEmail).orElseThrow(NoSuchElementException::new);
        Board target = boardRepository.findById(replyCreateForm.getBoardId()).orElseThrow(NoSuchElementException::new);
        replyRepository.save(replyCreateForm.toEntity(target, login));
        return "redirect:/board/" + replyCreateForm.getBoardId();
    }

}
