package com.mentoree.program.api;

import com.mentoree.global.exception.BindingFailureException;
import com.mentoree.global.exception.NoAuthorityException;
import com.mentoree.member.domain.Member;
import com.mentoree.participants.api.dto.ParticipantDTOCollection.ApplyRequest;
import com.mentoree.participants.domain.Participant;
import com.mentoree.program.domain.Program;
import com.mentoree.program.domain.ProgramRole;
import com.mentoree.member.repository.MemberRepository;
import com.mentoree.participants.repository.ParticipantRepository;
import com.mentoree.program.repository.ProgramRepository;
import com.mentoree.program.service.ProgramService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping("/api/programs")
@Api("Program Controller API")
public class ProgramAPIController {

    private final ProgramRepository programRepository;
    private final ProgramService programService;
    private final MemberRepository memberRepository;
    private final ParticipantRepository participantRepository;

    //== 프로그램 생성 ==//
    @ApiOperation(value = "프로그램 생성 요청", notes = "프로그램 생성 요청 후, 프로그램 개설자 참가 정보 갱신 반환")
    @PostMapping("/new")
    public ResponseEntity createProgram(@ApiParam(value = "생성 요청 폼", required = true) @Validated @RequestBody ProgramCreateDTO createForm, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            throw new BindingFailureException(bindingResult, "잘못된 프로그램 생성 요청입니다.");
        }

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
    @ApiOperation(value = "프로그램 리스트 요청", notes = "참가 할 수 있는 프로그램 전체 중 해당 페이지 프로그램 반환")
    @PostMapping("/list")
    public ResponseEntity getMoreList(@ApiParam(value = "프로그램 리스트 페이지 요청 폼", required = true) @Validated @RequestBody ProgramRequest data, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            throw new BindingFailureException(bindingResult, "잘못된 프로그램 리스트 요청입니다.");
        }

        PageRequest pageRequest = PageRequest.of(data.getPage(), 8);
        List<Long> programIds = data.getParticipatedPrograms() != null ? data.getParticipatedPrograms().stream().map(ParticipatedProgramDTO::getId).collect(Collectors.toList()) : null;

        Slice<ProgramInfoDTO> moreProgram = programRepository.findAllProgram(programIds, pageRequest);

        Map<String, Object> result = new HashMap<>();
        result.put("programList", moreProgram.getContent());
        result.put("hasNext", moreProgram.hasNext());

        return ResponseEntity.ok().body(result);
    }

    @ApiOperation(value = "추천 프로그램 리스트 요청", notes = "관심분야와 일치하는 참가 가능한 리스트 중 해당 페이지 프로그램 반환")
    @PostMapping("/list/recommend")
    public ResponseEntity getMoreRecommendList(@ApiParam(value = "추천 프로그램 리스트 페이지 요청", required = true) @Validated @RequestBody RecommendProgramRequest data, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            throw new BindingFailureException(bindingResult, "잘못된 추천 프로그램 리스트 요청입니다.");
        }

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
    @ApiOperation(value = "프로그램 상세 정보 열람", notes = "요청 프로그램 상세 정보 반환")
    @GetMapping("/{programId}")
    public ResponseEntity programInfoGet(@ApiParam(value = "요청 프로그램 Id", required = true) @PathVariable("programId") long programId) {
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
    @ApiOperation(value = "프로그램 참가 신청", notes = "프로그램 참가 신청 결과 반환")
    @PostMapping("/{programId}/join")
    public ResponseEntity applyProgram(@ApiParam(value = "참가 신청 폼", required = true) @Validated @RequestBody ApplyRequest applyRequest, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            throw new BindingFailureException(bindingResult, "잘못된 참가 신청 요청입니다.");
        }

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
    @ApiOperation(value = "프로그램 참가 신청자 리스트 요청", notes = "프로그램 정보 및 참가 신청자 리스트 반환")
    @GetMapping("/{programId}/applicants")
    public ResponseEntity programApplicationListGet(@ApiParam(value = "관리 프로그램 ID", required = true) @PathVariable("programId") long programId) {

        // 요청자 해당 프로그램 호스트 판별
        String loginEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!participantRepository.isHost(loginEmail, programId)) {
            throw new NoAuthorityException("프로그램 개최자가 아닙니다");
        }

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
    @ApiOperation(value = "프로그램 참가 승인 요청", notes = "프로그램 참가 승인 결과 반환")
    @PostMapping("/{programId}/applicants/accept")
    public ResponseEntity applicantAccept(@ApiParam(value = "승인 대상자 ID", required = true) @RequestBody Long memberId,
                                            @ApiParam(value = "관리 프로그램 ID", required = true) @PathVariable("programId") Long programId) {
        // 요청자 해당 프로그램 호스트 판별
        String loginEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!participantRepository.isHost(loginEmail, programId)) {
            throw new NoAuthorityException("프로그램 개최자가 아닙니다");
        }
        programService.approval(memberId, programId);

        Map<String, String> result = new HashMap<>();
        result.put("result", "success");

        return ResponseEntity.ok().body(result);
    }

    //== 프로그램 참가 거절 ==//
    @ApiOperation(value = "프로그램 참가 거절 요청", notes = "프로그램 참가 거절 결과 반환")
    @PostMapping("/{programId}/applicants/reject")
    public ResponseEntity applicantReject(@ApiParam(value = "거절 대상자 ID", required = true) @RequestBody Long memberId,
                                          @ApiParam(value = "관리 프로그램 ID", required = true) @PathVariable("programId") Long programId) {
        // 요청자 해당 프로그램 호스트 판별
        String loginEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!participantRepository.isHost(loginEmail, programId)) {
            throw new NoAuthorityException("프로그램 개최자가 아닙니다");
        }

        programService.reject(memberId, programId);

        Map<String, String> result = new HashMap<>();
        result.put("result", "success");

        return ResponseEntity.ok().body(result);
    }


}
