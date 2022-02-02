package com.mentoree.member.api;

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
    public ResponseEntity updateMemberProfile(@ApiParam(value = "수정된 회원 정보", required = true) @RequestBody MemberInfo updatedInfo) {
        log.info("update interest = {}", updatedInfo.getInterests());
        MemberInfo result = memberService.updateMemberInfo(updatedInfo);
        log.info("updateInfo = {}", result);
        return ResponseEntity.ok().body(result);
    }


}
