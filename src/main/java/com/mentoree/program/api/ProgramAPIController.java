package com.mentoree.program.api;

import com.mentoree.member.domain.Member;
import com.mentoree.participants.api.dto.ParticipantDTO.ApplyRequest;
import com.mentoree.participants.domain.Participant;
import com.mentoree.program.domain.Program;
import com.mentoree.program.domain.ProgramRole;
import com.mentoree.member.repository.MemberRepository;
import com.mentoree.participants.repository.ParticipantRepository;
import com.mentoree.program.repository.ProgramRepository;
import com.mentoree.program.service.ProgramService;
import com.mentoree.participants.api.dto.ParticipantDTO;
import com.mentoree.program.api.dto.ProgramRequestDTO;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static com.mentoree.program.api.dto.ProgramDTO.*;
import static com.mentoree.program.api.dto.ProgramRequestDTO.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProgramAPIController {

    private final ProgramRepository programRepository;
    private final ProgramService programService;
    private final MemberRepository memberRepository;
    private final ParticipantRepository participantRepository;

    //== 프로그램 생성 ==//
    @PostMapping("/program/create")
    public ResponseEntity createProgram(@RequestBody ProgramCreateDTO createForm) {
        log.info("program registry .....");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) auth.getPrincipal();
        Member findMember = memberRepository.findByEmail(email).orElseThrow(NoSuchElementException::new);

        createForm.setProgramRole(createForm.getMentor() ? ProgramRole.MENTOR.getKey() : ProgramRole.MENTEE.getKey());
        Program program = programService.createProgram(createForm, findMember);

        ParticipatedProgramDTO data = ParticipatedProgramDTO.builder().id(program.getId()).title(program.getProgramName()).build();
        return ResponseEntity.ok().body(data);
    }



    //== 프로그램 리스트 ==//
    @PostMapping("/program/list")
    public ResponseEntity getMoreList(@RequestBody ProgramRequest data) {
        int page = data.getPage();
        List<ParticipatedProgramDTO> programs = data.getParticipatedPrograms();
        PageRequest pageRequest = PageRequest.of(page, 8);
        List<Long> programIds = programs.stream().map(p -> p.getId()).collect(Collectors.toList());
        Slice<ProgramInfoDTO> moreProgram = programRepository.findAllProgram(programIds, pageRequest);

        Map<String, Object> result = new HashMap<>();
        result.put("programList", moreProgram.getContent());
        result.put("hasNext", moreProgram.hasNext());

        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/program/recommend/list")
    public ResponseEntity getMoreRecommendList(@RequestBody RecommendProgramRequest data) {

        int page = data.getPage();
        List<ParticipatedProgramDTO> programs = data.getParticipatedPrograms();
        List<String> interests = data.getInterests();

        PageRequest pageRequest = PageRequest.of(page, 3);
        List<Long> programIds = programs.stream().map(p -> p.getId()).collect(Collectors.toList());
        Slice<ProgramInfoDTO> moreRecommendPrograms = programRepository.findRecommendPrograms(programIds, interests, pageRequest);

        Map<String, Object> result = new HashMap<>();
        result.put("programRecommendList", moreRecommendPrograms.getContent());
        result.put("hasNext", moreRecommendPrograms.hasNext());
        return ResponseEntity.ok().body(result);
    }

    //== 프로그램 상세 정보 ==//
    @GetMapping("/program/{programId}/info")
    public ResponseEntity programInfoGet(@PathVariable("programId") long programId) {
        ProgramInfoDTO programInfoDTO = programRepository.findProgramById(programId).orElseThrow(NoSuchElementException::new);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loginEmail = (String) auth.getPrincipal();
        Participant host = participantRepository.findHost(programId).orElseThrow(NoSuchElementException::new);
        boolean isHost = loginEmail.equals(host.getMember().getEmail()) ? true : false;

        Map<String, Object> data = new HashMap<>();
        data.put("programInfo", programInfoDTO);
        data.put("isHost", isHost);

        return ResponseEntity.ok().body(data);
    }

    //== 프로그램 참가 신청 ==//
    @PostMapping("/program/{programId}/join")
    public ResponseEntity applyProgram(@RequestBody ApplyRequest applyRequest) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) auth.getPrincipal();
        Member loginMember = memberRepository.findByEmail(email).orElseThrow(NoSuchElementException::new);

        String msg = "";
        // 이 부분을 익셉션 발생으로 커스텀해서 -> 에러 코드랑 메시지를 보고 -> 이미 신청 메시지 띄우기
        if(programService.applyProgram(loginMember, applyRequest.getRole(), applyRequest.getProgramId(), applyRequest.getMessage())) {
            log.info("no history");
            msg = "success";
        } else {
            log.info("history");
            msg = "duplicate";
        }

        Map<String, String> result = new HashMap<>();
        result.put("result", msg);

        return ResponseEntity.ok().body(result);
    }


    //== 프로그램 참가자 관리 ==//
    @GetMapping("/program/{programId}/applicants")
    public ResponseEntity programApplicationListGet(@PathVariable("programId") long programId) {
        ProgramInfoDTO programInfoDTO = programRepository.findProgramById(programId).orElseThrow(NoSuchElementException::new);
        List<ApplyRequest> allApplicants = participantRepository.findAllApplicants(programId);
        Long currentNumMember = participantRepository.countCurrentMember(programId);
        Map<String, Object> data = new HashMap<>();
        data.put("programInfo", programInfoDTO);
        data.put("applicants", allApplicants);
        data.put("currentNumMember", currentNumMember);

        return ResponseEntity.ok().body(data);
    }

    //== 프로그램 참가 승인 ==//
    @PostMapping("/program/{programId}/applicants/accept")
    public ResponseEntity applicant(@RequestParam("memberId") long memberId, @PathVariable("programId") Long programId) {
        programService.approval(memberId, programId);

        Map<String, String> result = new HashMap<>();
        result.put("result", "success");

        return ResponseEntity.ok().body(result);
    }

    //== 프로그램 참가 거절 ==//
    @PostMapping("/program/{programId}/applicants/reject")
    public ResponseEntity applicantReject(@RequestParam("memberId") Long memberId, @PathVariable("programId") Long programId) {
        programService.reject(memberId, programId);

        Map<String, String> result = new HashMap<>();
        result.put("result", "success");

        return ResponseEntity.ok().body(result);
    }


}
