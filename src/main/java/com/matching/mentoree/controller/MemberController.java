package com.matching.mentoree.controller;

import com.matching.mentoree.config.security.util.JwtUtils;
import com.matching.mentoree.domain.Member;
import com.matching.mentoree.repository.MemberRepository;
import com.matching.mentoree.service.MemberService;
import com.matching.mentoree.service.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

import static com.matching.mentoree.service.dto.MemberDTO.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final JwtUtils jwtUtils;

    //== 로그인 ==//
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String msg, Model model) {
        model.addAttribute("error", msg);
        return "login";
    }

    //== 회원가입 ==//
    @GetMapping("/member/join")
    public String joinMemberGet(Model model) {
        model.addAttribute("createForm", new RegistrationRequest());
        return "register";
    }
    @PostMapping("/member/join")
    public String joinMemberPost(@ModelAttribute("createForm") RegistrationRequest createForm) {
        log.info("============= user try to join in ================");
        log.info("user info: " + createForm.toString());
        memberService.join(createForm);
        return "redirect:/login";
    }


    //== 프로필 관련 ==//
    @GetMapping("/member/profile/{email}")
    public String profileBrowseGet(@PathVariable("email") String email, Model model) {
        MemberInfo findMember = memberRepository.findMemberInfoByEmail(email).orElseThrow(NoSuchElementException::new);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = (String) auth.getPrincipal();

        boolean isLogin = userEmail.equals(email) ? true : false;
        model.addAttribute("profile", findMember);
        model.addAttribute("isLogin", isLogin);
        return "profileBrowse";
    }

    @GetMapping("/member/profile/edit")
    public String profileEditGet(Model model) {
        MemberInfo findMember = memberRepository.findMemberInfoByEmail((String)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).orElseThrow(NoSuchElementException::new);
        model.addAttribute("editForm", findMember);
        return "profileEdit";
    }

    @PostMapping("/member/profile/edit")
    public String profileEditPost(@ModelAttribute("editForm") MemberInfo editForm) {
        Member findMember = memberRepository.findByEmail((String)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).orElseThrow(NoSuchElementException::new);
        memberService.updateMemberInfo(editForm, findMember);
        return "redirect:/member/profile/" + findMember.getEmail();
    }

}
