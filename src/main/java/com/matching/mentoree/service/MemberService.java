package com.matching.mentoree.service;

import com.matching.mentoree.domain.*;
import com.matching.mentoree.repository.CategoryRepository;
import com.matching.mentoree.repository.MemberInterestRepository;
import com.matching.mentoree.repository.MemberRepository;
import com.matching.mentoree.repository.ParticipantRepository;
import com.matching.mentoree.service.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.matching.mentoree.service.dto.MemberDTO.*;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final ParticipantRepository participantRepository;
    private final MemberInterestRepository memberInterestRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    //== 회원가입 ==//
    @Transactional
    public void join(RegistrationRequest request) {
        memberRepository.save(request.toEntity(passwordEncoder, UserRole.USER));
    }

    @Transactional
    public boolean checkEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    //== 회원 정보 변경 ==//
    @Transactional
    public void updateMemberInfo(MemberInfo memberInfoDTO, Member login){
        if(memberInfoDTO.getNickname() != null && !memberInfoDTO.getNickname().equals(login.getNickname()))
            login.updateNickname(memberInfoDTO.getNickname());

        if(memberInfoDTO.getLink() != null && !memberInfoDTO.getLink().equals(login.getLink()))
            login.updateLink(memberInfoDTO.getLink());

        if(memberInfoDTO.getCategories() != null) {
            for (String c : memberInfoDTO.getCategories()) {
                if(!c.equals("default")) {
                    Category category = categoryRepository.findByCategoryName(c).orElseThrow(NoSuchElementException::new);
                    MemberInterest mi = MemberInterest.builder()
                            .member(login)
                            .category(category)
                            .build();
                    memberInterestRepository.save(mi);
                }
            }

        }
    }

    @Transactional
    public void changePassword(Member login, String password) {
        login.changePassword(password);
    }

    //== 회원 정보 열람 ==//
    @Transactional
    public MemberInfo getPersonalInfo(Member login) {
        return MemberInfo.builder()
                .email(login.getEmail())
                .nickname(login.getNickname())
                .link(login.getLink())
                .build();
    }


}
