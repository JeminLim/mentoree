package com.matching.mentoree.controller;

import com.matching.mentoree.config.security.util.JwtUtils;
import com.matching.mentoree.service.MemberService;
import com.matching.mentoree.service.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtUtils jwtUtils;

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String msg, Model model) {
        model.addAttribute("error", msg);
        return "login";
    }

    @GetMapping("/member/join")
    public String joinMemberGet(Model model) {
        model.addAttribute("createForm", new MemberDTO.RegistrationRequest());
        return "register";
    }
    @PostMapping("/member/join")
    public String joinMemberPost(@ModelAttribute("createForm") MemberDTO.RegistrationRequest createForm) {
        log.info("============= user try to join in ================");
        log.info("user info: " + createForm.toString());
        memberService.join(createForm);
        return "redirect:/login";
    }



}
