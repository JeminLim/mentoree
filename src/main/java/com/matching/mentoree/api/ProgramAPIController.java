package com.matching.mentoree.api;

import com.matching.mentoree.config.security.UserPrincipal;
import com.matching.mentoree.domain.Member;
import com.matching.mentoree.domain.Participant;
import com.matching.mentoree.domain.Program;
import com.matching.mentoree.domain.ProgramRole;
import com.matching.mentoree.repository.MemberRepository;
import com.matching.mentoree.repository.ParticipantRepository;
import com.matching.mentoree.repository.ProgramRepository;
import com.matching.mentoree.service.ProgramService;
import lombok.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
public class ProgramAPIController {

    private final ProgramRepository programRepository;
    private final ProgramService programService;
    private final MemberRepository memberRepository;
    private final ParticipantRepository participantRepository;

    //== 프로그램 참가 신청 ==//
    @PostMapping("/program/{programId}/join")
    public ResponseEntity applyProgram(@RequestParam("programId") long id,
                                       @RequestParam("role") String role,
                                       @RequestParam("message") String message) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) auth.getPrincipal();
        Member loginMember = memberRepository.findByEmail(email).orElseThrow(NoSuchElementException::new);

        String msg = "";
        if(programService.applyProgram(loginMember, ProgramRole.valueOf(role), id, message)) {
            msg = "success";
        } else {
            msg = "duplicate";
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        Map<String, String> result = new HashMap<>();
        result.put("result", msg);

        return new ResponseEntity(result, httpHeaders, HttpStatus.OK);
    }

    //== 프로그램 참가 승인 ==//
    @PostMapping("/program/{programId}/applicants/accept")
    public ResponseEntity applicant(@RequestParam("email") String email, @PathVariable("programId") Long programId) {
        programService.approval(email, programId);

        Map<String, String> result = new HashMap<>();
        result.put("result", "success");

        return new ResponseEntity(result, HttpStatus.OK);
    }

    //== 프로그램 참가 거절 ==//
    @PostMapping("/program/{programId}/applicants/reject")
    public ResponseEntity applicantReject(@RequestParam("email") String email, @PathVariable("programId") Long programId) {
        programService.reject(email, programId);

        Map<String, String> result = new HashMap<>();
        result.put("result", "success");

        return new ResponseEntity(result, HttpStatus.OK);
    }


}
