package com.mentoree.member.api;

import com.mentoree.member.api.dto.MemberDTO.MemberInfo;
import com.mentoree.member.repository.MemberRepository;
import com.mentoree.member.service.MemberService;
import com.mentoree.member.api.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberProfileAPIController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @GetMapping("/member/profile")
    public ResponseEntity getMemberProfile(@RequestParam("email") String email) {
        MemberInfo memberInfo = memberRepository.findMemberInfoByEmail(email).orElseThrow(NoSuchElementException::new);
        return ResponseEntity.ok().body(memberInfo);
    }

    @PostMapping("/member/profile")
    public ResponseEntity updateMemberProfile(@RequestBody MemberInfo updatedInfo) {
        log.info("update interest = {}", updatedInfo.getInterests());
        MemberInfo result = memberService.updateMemberInfo(updatedInfo);
        log.info("updateInfo = {}", result);
        return ResponseEntity.ok().body(result);
    }


}
