package com.matching.mentoree.api;

import com.matching.mentoree.config.security.UserPrincipal;
import com.matching.mentoree.domain.Member;
import com.matching.mentoree.domain.ProgramRole;
import com.matching.mentoree.repository.MemberRepository;
import com.matching.mentoree.repository.ProgramRepository;
import com.matching.mentoree.service.ProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
public class ProgramAPIController {

    private final ProgramRepository programRepository;
    private final ProgramService programService;
    private final MemberRepository memberRepository;

    @PostMapping("/program/{id}/join")
    public ResponseEntity applyProgram(@RequestParam("programId") long id, String role, String message) {

        String email = ((UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        Member loginMember = memberRepository.findByEmail(email).orElseThrow(NoSuchElementException::new);
        programService.applyProgram(loginMember, ProgramRole.valueOf(role), id, message);

        return new ResponseEntity("create success", HttpStatus.OK);
    }



}
