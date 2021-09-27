package com.matching.mentoree.controller;

import com.matching.mentoree.config.security.UserPrincipal;
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
import com.matching.mentoree.service.dto.ProgramDTO;
import com.matching.mentoree.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.NoSuchElementException;

import static com.matching.mentoree.service.dto.ProgramDTO.*;

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
        model.addAttribute("form", new ProgramDTO());
        return "programCreate";
    }

    @PostMapping("/program/registry")
    public String createProgramPost(@ModelAttribute("form") ProgramCreateDTO form) {
        String email = ((UserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
        Member findMember = memberRepository.findByEmail(email).orElseThrow(NoSuchElementException::new);

        form.setProgramRole(form.isMentor() == true ? ProgramRole.MENTOR.getKey() : ProgramRole.MENTEE.getKey());
        programService.createProgram(form, findMember);
        return "redirect:program";
    }

    //== 프로그램 열람 ==//
    @GetMapping("/program/{programId}")
    public String programGet(@PathVariable("programId") long programId, Model model) {
        String title = programRepository.findById(programId).orElseThrow(NoSuchElementException::new).getProgramName();
        List<MissionDTO> currentMission = missionRepository.findCurrentMission(programId);
        List<MissionDTO> endedMission = missionRepository.findEndedMission(programId);
        ProgramBrowseDTO programBrowseDTO = ProgramBrowseDTO.builder()
                .title(title)
                .curMission(currentMission)
                .endMission(endedMission).build();
        model.addAttribute("program", programBrowseDTO);
        return "program";
    }

    @GetMapping("/program/info/{programId}")
    public String programInfoGet(@PathVariable("programId") long programId, Model model) {
        ProgramInfoDTO programInfoDTO = programRepository.findProgramById(programId).orElseThrow(NoSuchElementException::new);
        model.addAttribute("program", programInfoDTO);

        String loginEmail = CommonUtil.getLoginEmail();
        Participant host = participantRepository.findHost(programId).orElseThrow(NoSuchElementException::new);
        boolean isHost = loginEmail.equals(host.getMember().getEmail()) ? true : false;
        model.addAttribute("isHost", isHost);
        return "programInfo";
    }


    //== 프로그램 참가자 관리 ==//
    @GetMapping("/program/{id}/join")
    public String programApplicationListGet(@PathVariable("id") long programId, Model model) {
        Program program = programRepository.findById(programId).orElseThrow(NoSuchElementException::new);
        List<Participant> allApplicants = participantRepository.findAllApplicants(program);
        model.addAttribute("program", program);
        model.addAttribute("applicants", allApplicants);
        return "participantList";
    }

    @PostMapping("/applicants/reject")
    public String applicantReject(@RequestParam("email") String email, HttpServletRequest request, RedirectAttributes redirect) {
        Program program = (Program) request.getAttribute("program");
        Participant findParticipant = participantRepository.findParticipantByEmailAndProgram(email, program.getId()).orElseThrow(NoSuchElementException::new);
        participantRepository.delete(findParticipant);

        List<Participant> newApplicants = participantRepository.findAllApplicants(program);
        redirect.addFlashAttribute("program", request.getAttribute("program"));
        redirect.addFlashAttribute("applicants", newApplicants);

        return "redirect:/program/" + program.getId() + "/join";
    }

    @PostMapping("/applicants/accept")
    public String applicant(@RequestParam("email") String email, HttpServletRequest request, RedirectAttributes redirect) {
        Program program = (Program) request.getAttribute("program");
        Participant findParticipant = participantRepository.findParticipantByEmailAndProgram(email, program.getId()).orElseThrow(NoSuchElementException::new);
        findParticipant.approve();

        List<Participant> newApplicants = participantRepository.findAllApplicants(program);
        // 현재 인원에 대한 것이 다시 repository 에서 찾는다면 캐싱에서 받아올것 같은데... 흠..
        Program updateProgram = programRepository.findById(program.getId()).orElseThrow(NoSuchElementException::new);
        redirect.addFlashAttribute("program", updateProgram);
        redirect.addFlashAttribute("applicants", newApplicants);
        return "redirect:/program/" + updateProgram.getId() + "/join";
    }


}
