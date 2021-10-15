package com.matching.mentoree.controller;

import com.matching.mentoree.repository.MemberRepository;
import com.matching.mentoree.service.MissionService;
import com.matching.mentoree.service.dto.MissionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;
    private final MemberRepository memberRepository;

    @GetMapping("/program/{programId}/mission/write")
    public String createMissionGet(@PathVariable("programId") Long programId, Model model) {
        MissionDTO missionDTO = new MissionDTO();
        missionDTO.setProgramId(programId);
        model.addAttribute("missionForm", missionDTO);
        return "missionCreate";
    }

    @PostMapping("/program/{programId}/mission/write")
    public String createMissionPost(@ModelAttribute("missionForm") MissionDTO createForm) {
        log.info("programId = " + createForm.getProgramId());
        missionService.createMission(createForm);
        return "redirect:/program/" + createForm.getProgramId();
    }




}
