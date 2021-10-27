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
import com.matching.mentoree.service.dto.ProgramDTO;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static com.matching.mentoree.service.dto.ProgramDTO.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ProgramAPIController {

    private final ProgramRepository programRepository;
    private final ProgramService programService;
    private final MemberRepository memberRepository;
    private final ParticipantRepository participantRepository;

    //== 프로그램 리스트 더 불러오기 ==//
    @GetMapping("/program/add/list")
    public ResponseEntity getMoreList(Model model, @RequestParam("page") int page) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) auth.getPrincipal();
        Member login = memberRepository.findByEmail(email).orElseThrow(NoSuchElementException::new);

        PageRequest pageRequest = PageRequest.of(page, 8);
        Slice<ProgramInfoDTO> moreProgram = programRepository.findAllProgram(login, pageRequest);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        Map<String, Object> result = new HashMap<>();
        result.put("result", "success");
        result.put("moreProgram", moreProgram.getContent());
        result.put("hasNext", moreProgram.hasNext());

        return new ResponseEntity(result, httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/program/add/recommend/list")
    public ResponseEntity getMoreRecommendList(Model model, @RequestParam("page") int page) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) auth.getPrincipal();
        Member login = memberRepository.findByEmail(email).orElseThrow(NoSuchElementException::new);

        PageRequest pageRequest = PageRequest.of(page, 3);
        Slice<ProgramInfoDTO> moreRecommendPrograms = programRepository.findRecommendPrograms(login, pageRequest);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        Map<String, Object> result = new HashMap<>();
        result.put("result", "success");
        result.put("programRecommendList", moreRecommendPrograms.getContent());
        result.put("hasNext", moreRecommendPrograms.hasNext());
        return new ResponseEntity(result, httpHeaders, HttpStatus.OK);
    }


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
