package com.matching.mentoree.service;

import com.matching.mentoree.domain.Member;
import com.matching.mentoree.domain.Participant;
import com.matching.mentoree.domain.UserRole;
import com.matching.mentoree.repository.MemberRepository;
import com.matching.mentoree.repository.ParticipantRepository;
import com.matching.mentoree.service.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final ParticipantRepository participantRepository;
    private final PasswordEncoder passwordEncoder;

    //== 회원가입 ==//
    @Transactional
    public void join(MemberDTO.RegistrationRequest request) {
        memberRepository.save(request.toEntity(passwordEncoder, UserRole.USER));
    }

    @Transactional
    public boolean checkEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    //== 회원 정보 변경 ==//
    @Transactional
    public void updateMemberInfo(MemberDTO.MemberInfo memberInfoDTO, Member login){
        if(memberInfoDTO.getNickname() != null && !memberInfoDTO.getNickname().equals(login.getNickname()))
            login.updateNickname(memberInfoDTO.getNickname());

        if(memberInfoDTO.getLink() != null && !memberInfoDTO.getLink().equals(login.getLink()))
            login.updateLink(memberInfoDTO.getLink());

        if(memberInfoDTO.getImgUrl() != null && !memberInfoDTO.getImgUrl().equals(login.getOriginProfileImgUrl()))
            ;
    }

    @Transactional
    public void changePassword(Member login, String password) {
        login.changePassword(password);
    }

    //== 회원 정보 열람 ==//
    @Transactional
    public MemberDTO.MemberInfo getPersonalInfo(Member login) {
        return MemberDTO.MemberInfo.builder()
                .email(login.getEmail())
                .nickname(login.getNickname())
                .imgUrl(login.getThumbnailImgUrl())
                .link(login.getLink())
                .programList(participantRepository.findParticipateHistory(login).stream()
                                    .map(Participant::getProgram)
                                    .collect(Collectors.toList()))
                .build();
    }


}
