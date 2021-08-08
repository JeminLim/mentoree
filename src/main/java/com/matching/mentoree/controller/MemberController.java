package com.matching.mentoree.controller;

import com.matching.mentoree.service.MemberService;
import com.matching.mentoree.service.dto.MemberCreateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/member/join")
    public String joinMemberGet(Model model) {
        model.addAttribute("createForm", new MemberCreateDTO());
        return "/register";
    }
    @PostMapping("/member/join")
    public String joinMemberPost(@ModelAttribute("createForm") MemberCreateDTO createForm) {
        memberService.join(createForm);
        return "redirect:/login";
    }



    @PostMapping("/member/join/check/email")
    @ResponseBody
    public boolean checkEmail(String email) {
       return memberService.checkEmail(email);
    }



}
