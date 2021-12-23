package com.mentoree.member.api;

import com.mentoree.member.repository.MemberRepository;
import com.mentoree.member.service.MemberService;
import com.mentoree.member.api.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberRegisterAPIController {

    private final MemberRepository memberRepository;
    private final MemberService memberService;

    //== 회원가입 ==//
    @PostMapping("/join")
    public ResponseEntity joinMemberPost(@RequestBody MemberDTO.RegistrationRequest registrationForm) {
        memberService.join(registrationForm);
        return ResponseEntity.ok().body("success");
    }

    @PostMapping("/join/check/email")
    public ResponseEntity checkEmail(@RequestParam("email") String email) {
        boolean result = memberRepository.existsByEmail(email);
        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/join/check/nickname")
    public ResponseEntity checkNickname(@RequestParam("nickname") String nickname) {
        return ResponseEntity.ok().body(memberRepository.existsByNickname(nickname));
    }

}
