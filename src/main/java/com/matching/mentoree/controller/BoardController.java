package com.matching.mentoree.controller;

import com.matching.mentoree.domain.Board;
import com.matching.mentoree.domain.Member;
import com.matching.mentoree.domain.Mission;
import com.matching.mentoree.repository.BoardRepository;
import com.matching.mentoree.repository.MemberRepository;
import com.matching.mentoree.repository.MissionRepository;
import com.matching.mentoree.repository.ReplyRepository;
import com.matching.mentoree.service.BoardService;
import com.matching.mentoree.service.dto.ReplyDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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


    //== 전체 Board 열람 ==//
    @GetMapping("/program/{programId}/mission/{missionId}/board/list")
    public String boardListGet(@PathVariable("programId") Long programId, @PathVariable("missionId") Long missionId, Model model) {
        Mission mission = missionRepository.findById(missionId).orElseThrow(NoSuchElementException::new);
        model.addAttribute("mission", mission);
        List<BoardInfo> boards = boardRepository.findAllBoardInfoById(missionId);
        model.addAttribute("boardList", boards);
        model.addAttribute("programId", programId);
        return "board";
    }

    //== 개별 Board 열람 ==//
    @GetMapping("/program/{programId}/mission/{missionId}/board/{boardId}")
    public String boardGet(@PathVariable("programId") Long programId, @PathVariable("boardId") long boardId, Model model) {
        model.addAttribute("programId", programId);

        BoardInfo findBoard = boardRepository.findBoardInfoById(boardId).orElseThrow(NoSuchElementException::new);
        model.addAttribute("boardInfo", findBoard);

        List<ReplyDTO> allReplies = replyRepository.findRepliesAllByBoard(boardId);
        model.addAttribute("replyList", allReplies);

        ReplyDTO replyCreateForm = new ReplyDTO();
        replyCreateForm.setBoardId(boardId);
        model.addAttribute("replyCreateForm", replyCreateForm);
        return "boardInfo";
    }

    //== Board 작성 ==//
    @GetMapping("/program/{programId}/mission/{missionId}/board/write")
    public String createBoardGet(@PathVariable("programId") Long programId, @PathVariable("missionId") Long missionId, Model model) {
        BoardInfo boardDTO = new BoardInfo();
        boardDTO.setMissionId(missionId);
        Member login = memberRepository.findByEmail((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).orElseThrow(NoSuchElementException::new);
        boardDTO.setWriterNickname(login.getNickname());

        model.addAttribute("programId", programId);
        model.addAttribute("boardForm", boardDTO);

        return "boardCreate";
    }

    @PostMapping("/program/{programId}/mission/{missionId}/board/write")
    public String createBoardPost(@ModelAttribute("boardForm") BoardInfo createForm) {
        Member login = memberRepository.findByEmail((String)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).orElseThrow(NoSuchElementException::new);
        boardService.saveBoard(createForm, login);
        return "redirect:" + createForm.getMissionId() + "/mission/list";
    }


    //== 댓글 작성 관련 ==//
    @PostMapping("/program/{programId}/mission/{missionId}/board/{boardId}/reply/write")
    public String replyWrite(@ModelAttribute("replyCreateForm") ReplyDTO replyCreateForm) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loginEmail = (String) auth.getPrincipal();
        Member login = memberRepository.findByEmail(loginEmail).orElseThrow(NoSuchElementException::new);
        Board target = boardRepository.findById(replyCreateForm.getBoardId()).orElseThrow(NoSuchElementException::new);
        replyRepository.save(replyCreateForm.toEntity(target, login));
        return "redirect:/board/" + replyCreateForm.getBoardId();
    }

}
