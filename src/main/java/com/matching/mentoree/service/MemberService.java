package com.matching.mentoree.service;

import com.matching.mentoree.domain.Member;
import com.matching.mentoree.domain.Participant;
import com.matching.mentoree.domain.Program;
import com.matching.mentoree.repository.MemberRepository;
import com.matching.mentoree.repository.ParticipantRepository;
import com.matching.mentoree.repository.ProgramRepository;
import com.matching.mentoree.service.dto.MemberCreateDTO;
import com.matching.mentoree.service.dto.MemberInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final ParticipantRepository participantRepository;

    //== 회원가입 ==//
    @Transactional
    public void join(MemberCreateDTO createForm) {
        memberRepository.save(createForm.toEntity());
    }

    @Transactional
    public boolean checkEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    //== 회원 정보 변경 ==//
    @Transactional
    public void updateMemberInfo(MemberInfoDTO memberInfoDTO, Member login){
        if(memberInfoDTO.getNickname() != null && !memberInfoDTO.getNickname().equals(login.getNickname()))
            login.updateNickname(memberInfoDTO.getNickname());

        if(memberInfoDTO.getLink() != null && !memberInfoDTO.getLink().equals(login.getLink()))
            login.updateLink(memberInfoDTO.getLink());

        if(memberInfoDTO.getOriginProfileImgUrl() != null && !memberInfoDTO.getOriginProfileImgUrl().equals(login.getOriginProfileImgUrl()))
            login.updateProfileImg(memberInfoDTO.getOriginProfileImgUrl(), memberInfoDTO.getThumbnailImgUrl());
    }

    @Transactional
    public void changePassword(Member login, String password) {
        login.changePassword(password);
    }

    //== 회원 정보 열람 ==//
    @Transactional
    public MemberInfoDTO getPersonalInfo(Member login) {
        return MemberInfoDTO.builder()
                .username(login.getUsername())
                .email(login.getEmail())
                .nickname(login.getNickname())
                .originProfileImgUrl(login.getOriginProfileImgUrl())
                .thumbnailImgUrl(login.getThumbnailImgUrl())
                .link(login.getLink())
                .programList(participantRepository.findParticipateHistory(login).stream()
                                    .map(Participant::getProgram)
                                    .collect(Collectors.toList()))
                .build();
    }


}
