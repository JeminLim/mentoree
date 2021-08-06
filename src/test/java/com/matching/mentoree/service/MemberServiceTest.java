package com.matching.mentoree.service;

import com.matching.mentoree.domain.Member;
import com.matching.mentoree.repository.MemberRepository;
import com.matching.mentoree.repository.ParticipantRepository;
import com.matching.mentoree.service.dto.MemberCreateDTO;
import com.matching.mentoree.service.dto.MemberInfoDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ParticipantRepository participantRepository;
    @InjectMocks
    private MemberService memberService;

    private Member login;

    @BeforeEach
    public void setUp() {
        login = Member.builder()
                .username("tester")
                .email("test@email.com")
                .userPassword("1234")
                .nickname("testNick")
                .build();
    }

    @Test
    @DisplayName("유저 회원가입 테스트")
    public void create_user_test() throws Exception {
        //given
        MemberCreateDTO createDTO = MemberCreateDTO.builder()
                .username("tester")
                .userPassword("1234")
                .email("test@email.com")
                .nickname("testNick")
                .build();

        given(memberRepository.save(argThat(member -> member.getEmail().equals("test@email.com"))))
                .willReturn(createDTO.toEntity());
        //when
        memberService.join(createDTO);
        //then
        verify(memberRepository).save(argThat(member -> member.getEmail().equals("test@email.com")));
    }

    @Test
    @DisplayName("회원 정보 변경 테스트")
    public void update_member_info() throws Exception {
        //given
        MemberInfoDTO dto = MemberInfoDTO.builder()
                .nickname("changedNick")
                .link("changedLink")
                .originProfileImgUrl("changedUrl")
                .thumbnailImgUrl("changedThumbUrl")
                .build();

        //when
        memberService.updateMemberInfo(dto, login);

        //then
        assertThat(login.getNickname()).isEqualTo(dto.getNickname());
        assertThat(login.getLink()).isEqualTo(dto.getLink());
        assertThat(login.getOriginProfileImgUrl()).isEqualTo(dto.getOriginProfileImgUrl());
        assertThat(login.getThumbnailImgUrl()).isEqualTo(dto.getThumbnailImgUrl());
    }

    @Test
    @DisplayName("null 값 미변경 테스트")
    public void update_not_null_member_info() throws Exception {
        //given
        login.updateLink("originLink");

        MemberInfoDTO dto = MemberInfoDTO.builder()
                .originProfileImgUrl("changedUrl")
                .thumbnailImgUrl("changedThumbUrl")
                .build();

        //when
        memberService.updateMemberInfo(dto, login);

        //then
        assertThat(login.getNickname()).isEqualTo("testNick");
        assertThat(login.getLink()).isEqualTo("originLink");
    }

    @Test
    public void get_member_info() throws Exception {
        //when
        memberService.getPersonalInfo(login);
        //then
        verify(participantRepository, times(1)).findParticipateHistory(login);
    }




}