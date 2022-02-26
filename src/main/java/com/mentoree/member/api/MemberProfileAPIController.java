package com.mentoree.member.api;

import com.mentoree.config.security.UserPrincipal;
import com.mentoree.global.exception.BindingFailureException;
import com.mentoree.global.exception.NoAuthorityException;
import com.mentoree.member.api.dto.MemberDTO.MemberInfo;
import com.mentoree.member.repository.MemberRepository;
import com.mentoree.member.service.MemberService;
import com.mentoree.member.api.dto.MemberDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Api("Member Profile Controller API")
public class MemberProfileAPIController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @ApiOperation(value = "회원 프로필 정보", notes = "회원 프로필 정보 반환")
    @GetMapping("/profile")
    public ResponseEntity getMemberProfile(@ApiParam(value = "타겟 회원의 이메일", required = true) @RequestParam("email") String email) {
        MemberInfo memberInfo = memberRepository.findMemberInfoByEmail(email).orElseThrow(NoSuchElementException::new);
        return ResponseEntity.ok().body(memberInfo);
    }

    @ApiOperation(value = "회원 프로필 정보 업데이트", notes = "회원 프로필 정보 수정 및 결과 반환")
    @PostMapping("/profile")
    public ResponseEntity updateMemberProfile(@ApiParam(value = "수정된 회원 정보", required = true) @Validated @RequestBody MemberInfo updatedInfo, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            throw new BindingFailureException(bindingResult);
        }
        String loginEmail = ((UserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
        if(!updatedInfo.getEmail().equals(loginEmail)) {
            throw new NoAuthorityException("해당 사용자가 아닙니다");
        }

        MemberInfo result = memberService.updateMemberInfo(updatedInfo);
        return ResponseEntity.ok().body(result);
    }


}
