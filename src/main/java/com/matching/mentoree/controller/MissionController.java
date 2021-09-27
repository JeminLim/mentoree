package com.matching.mentoree.controller;

import com.matching.mentoree.domain.Member;
import com.matching.mentoree.repository.MemberRepository;
import com.matching.mentoree.service.MissionService;
import com.matching.mentoree.service.dto.MissionDTO;
import com.matching.mentoree.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;
    private final MemberRepository memberRepository;

    @GetMapping("/{programId}/mission/write")
    public String createMissionGet(@PathVariable("programId") Long programId, Model model) {
        MissionDTO missionDTO = new MissionDTO();
        missionDTO.setProgramId(programId);
        model.addAttribute("missionForm", new MissionDTO());
        return "missionCreate";
    }

    @PostMapping("/mission/write")
    public String createMissionPost(@ModelAttribute("missionForm") MissionDTO createForm) {
        String loginEmail = CommonUtil.getLoginEmail();
        Member login = memberRepository.findByEmail(loginEmail).orElseThrow(NoSuchElementException::new);
        missionService.createMission(createForm, login);
        return "redirect:/program/" + createForm.getProgramId();
    }




}
