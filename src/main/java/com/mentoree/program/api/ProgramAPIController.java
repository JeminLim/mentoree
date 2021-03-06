package com.mentoree.program.api;

import com.mentoree.config.security.UserPrincipal;
import com.mentoree.global.exception.BindingFailureException;
import com.mentoree.global.exception.NoAuthorityException;
import com.mentoree.member.domain.Member;
import com.mentoree.participants.api.dto.ParticipantDTOCollection;
import com.mentoree.participants.api.dto.ParticipantDTOCollection.Applicant;
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

    //== ???????????? ?????? ==//
    @ApiOperation(value = "???????????? ?????? ??????", notes = "???????????? ?????? ?????? ???, ???????????? ????????? ?????? ?????? ?????? ??????")
    @PostMapping("/new")
    public ResponseEntity createProgram(@ApiParam(value = "?????? ?????? ???", required = true) @Validated @RequestBody ProgramCreateDTO createForm, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            throw new BindingFailureException(bindingResult, "????????? ???????????? ?????? ???????????????.");
        }
        String email = ((UserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
        Member findMember = memberRepository.findByEmail(email).orElseThrow(NoSuchElementException::new);

        createForm.setProgramRole(createForm.getMentor() ? ProgramRole.MENTOR.getKey() : ProgramRole.MENTEE.getKey());
        Program program = programService.createProgram(createForm, findMember);

        ParticipatedProgramDTO data = ParticipatedProgramDTO.builder().id(program.getId()).title(program.getProgramName()).build();
        return ResponseEntity.ok().body(data);
    }



    //== ???????????? ????????? ==//
    @ApiOperation(value = "???????????? ????????? ??????", notes = "?????? ??? ??? ?????? ???????????? ?????? ??? ?????? ????????? ???????????? ??????")
    @PostMapping("/list")
    public ResponseEntity getMoreList(@ApiParam(value = "???????????? ????????? ????????? ?????? ???", required = true) @Validated @RequestBody ProgramRequest data, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            throw new BindingFailureException(bindingResult, "????????? ???????????? ????????? ???????????????.");
        }

        PageRequest pageRequest = PageRequest.of(data.getPage(), 8);
        List<Long> programIds = data.getParticipatedPrograms() != null ? data.getParticipatedPrograms().stream().map(ParticipatedProgramDTO::getId).collect(Collectors.toList()) : null;

        Slice<ProgramInfoDTO> moreProgram = programRepository.findAllProgram(programIds, pageRequest);

        Map<String, Object> result = new HashMap<>();
        result.put("programList", moreProgram.getContent());
        result.put("hasNext", moreProgram.hasNext());

        return ResponseEntity.ok().body(result);
    }

    @ApiOperation(value = "?????? ???????????? ????????? ??????", notes = "??????????????? ???????????? ?????? ????????? ????????? ??? ?????? ????????? ???????????? ??????")
    @PostMapping("/list/recommend")
    public ResponseEntity getMoreRecommendList(@ApiParam(value = "?????? ???????????? ????????? ????????? ??????", required = true) @Validated @RequestBody RecommendProgramRequest data, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            throw new BindingFailureException(bindingResult, "????????? ?????? ???????????? ????????? ???????????????.");
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

    //== ???????????? ?????? ?????? ==//
    @ApiOperation(value = "???????????? ?????? ?????? ??????", notes = "?????? ???????????? ?????? ?????? ??????")
    @GetMapping("/{programId}")
    public ResponseEntity programInfoGet(@ApiParam(value = "?????? ???????????? Id", required = true) @PathVariable("programId") long programId) {
        ProgramInfoDTO programInfoDTO = programRepository.findProgramById(programId).orElseThrow(NoSuchElementException::new);
        String loginEmail = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
        Participant host = participantRepository.findHost(programId).orElseThrow(NoSuchElementException::new);
        boolean isHost = loginEmail.equals(host.getMember().getEmail()) ? true : false;

        Map<String, Object> data = new HashMap<>();
        data.put("programInfo", programInfoDTO);
        data.put("isHost", isHost);

        return ResponseEntity.ok().body(data);
    }

    //== ???????????? ?????? ?????? ==//
    @ApiOperation(value = "???????????? ?????? ??????", notes = "???????????? ?????? ?????? ?????? ??????")
    @PostMapping("/{programId}/join")
    public ResponseEntity applyProgram(@ApiParam(value = "?????? ?????? ???", required = true) @Validated @RequestBody ApplyRequest applyRequest, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            throw new BindingFailureException(bindingResult, "????????? ?????? ?????? ???????????????.");
        }

        String email = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
        Member loginMember = memberRepository.findByEmail(email).orElseThrow(NoSuchElementException::new);

        String msg = "";
        // ??? ????????? ????????? ???????????? ??????????????? -> ?????? ????????? ???????????? ?????? -> ?????? ?????? ????????? ?????????
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


    //== ???????????? ????????? ?????? ==//
    @ApiOperation(value = "???????????? ?????? ????????? ????????? ??????", notes = "???????????? ?????? ??? ?????? ????????? ????????? ??????")
    @GetMapping("/{programId}/applicants")
    public ResponseEntity programApplicationListGet(@ApiParam(value = "?????? ???????????? ID", required = true) @PathVariable("programId") long programId) {

        // ????????? ?????? ???????????? ????????? ??????
        String loginEmail = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
        if(!participantRepository.isHost(loginEmail, programId)) {
            throw new NoAuthorityException("???????????? ???????????? ????????????");
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

    //== ???????????? ?????? ?????? ==//
    @ApiOperation(value = "???????????? ?????? ?????? ??????", notes = "???????????? ?????? ?????? ?????? ??????")
    @PostMapping("/{programId}/applicants/accept")
    public ResponseEntity applicantAccept(@ApiParam(value = "?????? ?????????", required = true) @RequestBody Applicant member,
                                          @ApiParam(value = "?????? ???????????? ID", required = true) @PathVariable("programId") Long programId) {
        // ????????? ?????? ???????????? ????????? ??????
        String loginEmail = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
        if(!participantRepository.isHost(loginEmail, programId)) {
            throw new NoAuthorityException("???????????? ???????????? ????????????");
        }
        programService.approval(member.getMemberId(), programId);

        Map<String, String> result = new HashMap<>();
        result.put("result", "success");

        return ResponseEntity.ok().body(result);
    }

    //== ???????????? ?????? ?????? ==//
    @ApiOperation(value = "???????????? ?????? ?????? ??????", notes = "???????????? ?????? ?????? ?????? ??????")
    @PostMapping("/{programId}/applicants/reject")
    public ResponseEntity applicantReject(@ApiParam(value = "?????? ?????????", required = true) @RequestBody Applicant member,
                                          @ApiParam(value = "?????? ???????????? ID", required = true) @PathVariable("programId") Long programId) {
        // ????????? ?????? ???????????? ????????? ??????
        String loginEmail = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
        if(!participantRepository.isHost(loginEmail, programId)) {
            throw new NoAuthorityException("???????????? ???????????? ????????????");
        }

        programService.reject(member.getMemberId(), programId);

        Map<String, String> result = new HashMap<>();
        result.put("result", "success");

        return ResponseEntity.ok().body(result);
    }


}
