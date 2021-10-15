package com.matching.mentoree.controller;

import com.matching.mentoree.config.security.UserPrincipal;
import com.matching.mentoree.domain.Member;
import com.matching.mentoree.repository.MemberRepository;
import com.matching.mentoree.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;

import static com.matching.mentoree.service.dto.ProgramDTO.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProgramRepository programRepository;
    private final MemberRepository memberRepository;

    @GetMapping("/")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) auth.getPrincipal();
        Member login = memberRepository.findByEmail(email).orElseThrow(NoSuchElementException::new);

        // 나중에 페이징으로 쿼리 수정
        List<ProgramInfoDTO> recommendPrograms = programRepository.findRecommendPrograms(login);
        model.addAttribute("programRecommendList", recommendPrograms);
        List<ProgramInfoDTO> allProgram = programRepository.findAllProgram();
        model.addAttribute("programList", allProgram);

        return "index";
    }

}
