package com.mentoree.member.service;

import com.mentoree.category.repository.CategoryRepository;
import com.mentoree.member.api.dto.MemberDTO.RegistrationRequest;
import com.mentoree.member.repository.MemberInterestRepository;
import com.mentoree.member.repository.MemberRepository;
import com.mentoree.member.api.dto.MemberDTO;
import com.mentoree.category.domain.Category;
import com.mentoree.member.domain.Member;
import com.mentoree.member.domain.MemberInterest;
import com.mentoree.global.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static com.mentoree.member.api.dto.MemberDTO.*;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberInterestRepository memberInterestRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    //== 회원가입 ==//
    @Transactional
    public Member join(RegistrationRequest request) {
        return memberRepository.save(request.toEntity(passwordEncoder, UserRole.USER));
    }

    //== 회원 정보 변경 ==//
    @Transactional
    public MemberInfo updateMemberInfo(MemberInfo memberInfoDTO){
        Member findMember = memberRepository.findByEmail(memberInfoDTO.getEmail()).orElseThrow(NoSuchElementException::new);
        List<MemberInterest> memberInterest = memberInterestRepository.findInterests(findMember.getId());

        if(memberInfoDTO.getNickname() != null && !memberInfoDTO.getNickname().equals(findMember.getNickname()))
            findMember.updateNickname(memberInfoDTO.getNickname());

        if(memberInfoDTO.getLink() != null && !memberInfoDTO.getLink().equals(findMember.getLink()))
            findMember.updateLink(memberInfoDTO.getLink());

        if(memberInfoDTO.getInterests() != null) {
            if(memberInterest.size() > 0) {
                for (int i = 0; i < memberInfoDTO.getInterests().size(); i++) {
                    String categoryName = memberInfoDTO.getInterests().get(i);
                    Category category = categoryRepository.findByCategoryName(categoryName).orElseThrow(NoSuchElementException::new);
                    memberInterest.get(i).changeInterest(category);
                }
            } else {
                for (int i = 0; i < memberInfoDTO.getInterests().size(); i++) {
                    String categoryName = memberInfoDTO.getInterests().get(i);
                    Category category = categoryRepository.findByCategoryName(categoryName).orElseThrow(NoSuchElementException::new);
                    MemberInterest interest = MemberInterest.builder().member(findMember).category(category).build();
                    memberInterestRepository.save(interest);
                    memberInterest.add(interest);
                }
            }
        }
        MemberInfo result = MemberInfo.of(findMember);
        result.setInterests(memberInterest.stream().map(i -> i.getCategory().getCategoryName()).collect(Collectors.toList()));
        result.setParticipatedPrograms(memberInfoDTO.getParticipatedPrograms());

        return result;
    }

    @Transactional
    public void changePassword(Member login, String password) {
        login.changePassword(passwordEncoder.encode(password));
    }

}
