package com.mentoree.member.api;

import com.mentoree.member.repository.MemberRepository;
import com.mentoree.member.service.MemberService;
import com.mentoree.member.api.dto.MemberDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.mentoree.member.api.dto.MemberDTO.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Api("Member Join Controller API")
public class MemberRegisterAPIController {

    private final MemberRepository memberRepository;
    private final MemberService memberService;

    //== 회원가입 ==//
    @ApiOperation(value = "회원 가입 요청", notes = "회원 가입을 요청의 결과를 반환")
    @PostMapping("/join")
    public ResponseEntity joinMemberPost(@Validated @RequestBody @ApiParam(value = "회원 가입 요청 폼", required = true) MemberRegistRequest registForm, BindingResult bindingResult) {
        memberService.join(registForm);
        return ResponseEntity.ok().body("success");
    }

    @ApiOperation(value = "이메일 중복 체크", notes = "이메일 중복 체크 결과 반환")
    @PostMapping("/join/email-check")
    public ResponseEntity checkEmail(@ApiParam(value = "사용자 입력 이메일", required = true) @RequestParam("email") String email) {
        boolean result = memberRepository.existsByEmail(email);
        return ResponseEntity.ok().body(result);
    }

    @ApiOperation(value = "닉네임 중복 체크", notes = "닉네임 중복 체크 결과 반환")
    @PostMapping("/join/nickname-check")
    public ResponseEntity checkNickname(@ApiParam(value = "사용자 입력 닉네임", required = true) @RequestParam("nickname") String nickname) {
        return ResponseEntity.ok().body(memberRepository.existsByNickname(nickname));
    }

}
