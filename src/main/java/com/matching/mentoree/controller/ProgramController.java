package com.matching.mentoree.controller;

import com.matching.mentoree.domain.Member;
import com.matching.mentoree.domain.Participant;
import com.matching.mentoree.domain.Program;
import com.matching.mentoree.domain.ProgramRole;
import com.matching.mentoree.repository.MemberRepository;
import com.matching.mentoree.repository.MissionRepository;
import com.matching.mentoree.repository.ParticipantRepository;
import com.matching.mentoree.repository.ProgramRepository;
import com.matching.mentoree.service.ProgramService;
import com.matching.mentoree.service.dto.MissionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.NoSuchElementException;

import static com.matching.mentoree.service.dto.ProgramDTO.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ProgramController {

    private final ProgramService programService;
    private final ProgramRepository programRepository;
    private final MemberRepository memberRepository;
    private final ParticipantRepository participantRepository;
    private final MissionRepository missionRepository;

    //== 프로그램 등록 ==//
    @GetMapping("/program/registry")
    public String createProgramGet(Model model) {
        model.addAttribute("form", new ProgramCreateDTO());
        return "programCreate";
    }

    @PostMapping("/program/registry")
    public String createProgramPost(@ModelAttribute("form") ProgramCreateDTO form) {
        log.info("program registry .....");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) auth.getPrincipal();
        Member findMember = memberRepository.findByEmail(email).orElseThrow(NoSuchElementException::new);

        form.setProgramRole(form.getMentor() ? ProgramRole.MENTOR.getKey() : ProgramRole.MENTEE.getKey());
        programService.createProgram(form, findMember);
        return "redirect:/";
    }

    //== 프로그램 열람 ==//
    @GetMapping("/program/{programId}")
    public String programGet(@PathVariable("programId") long programId, Model model) {
        String title = programRepository.findById(programId).orElseThrow(NoSuchElementException::new).getProgramName();
        List<MissionDTO> currentMission = missionRepository.findCurrentMission(programId);
        List<MissionDTO> endedMission = missionRepository.findEndedMission(programId);
        ProgramBrowseDTO programBrowseDTO = ProgramBrowseDTO.builder()
                .programId(programId)
                .title(title)
                .curMission(currentMission)
                .endMission(endedMission).build();
        model.addAttribute("program", programBrowseDTO);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loginEmail = (String) auth.getPrincipal();


        Participant loginParticipant = participantRepository.findParticipantByEmailAndProgram(loginEmail, programId).orElseThrow(NoSuchElementException::new);
        boolean isMentor = loginParticipant.isApproval() && loginParticipant.getRole().equals(ProgramRole.MENTOR) ? true : false;
        model.addAttribute("isMentor", isMentor);

        return "program";
    }

    @GetMapping("/program/info/{programId}")
    public String programInfoGet(@PathVariable("programId") long programId, Model model) {
        ProgramInfoDTO programInfoDTO = programRepository.findProgramById(programId).orElseThrow(NoSuchElementException::new);
        model.addAttribute("program", programInfoDTO);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loginEmail = (String) auth.getPrincipal();
        Participant host = participantRepository.findHost(programId).orElseThrow(NoSuchElementException::new);
        boolean isHost = loginEmail.equals(host.getMember().getEmail()) ? true : false;
        model.addAttribute("isHost", isHost);
        return "programInfo";
    }

    //== 프로그램 참가자 관리 ==//
    @GetMapping("/program/{programId}/applicants")
    public String programApplicationListGet(@PathVariable("programId") long programId, Model model) {
        Program program = programRepository.findById(programId).orElseThrow(NoSuchElementException::new);
        List<Participant> allApplicants = participantRepository.findAllApplicants(program);
        model.addAttribute("program", program);
        model.addAttribute("applicants", allApplicants);
        return "participantList";
    }


}
