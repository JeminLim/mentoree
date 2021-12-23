package com.mentoree.service;

import com.mentoree.category.domain.Category;
import com.mentoree.category.repository.CategoryRepository;
import com.mentoree.global.domain.UserRole;
import com.mentoree.member.api.dto.MemberDTO.MemberInfo;
import com.mentoree.member.api.dto.MemberDTO.RegistrationRequest;
import com.mentoree.member.domain.Member;
import com.mentoree.member.domain.MemberInterest;
import com.mentoree.member.repository.MemberInterestRepository;
import com.mentoree.member.repository.MemberRepository;
import com.mentoree.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MemberInterestRepository memberInterestRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("회원가입 테스트")
    public void join() throws Exception {
        //given
        RegistrationRequest regForm = RegistrationRequest.builder()
                .email("test@email.com")
                .password("1234")
                .memberName("tester")
                .nickname("testerNick")
                .build();
        Member member = regForm.toEntity(passwordEncoder, UserRole.USER);
        when(memberRepository.save(any())).thenReturn(member);

        //when
        Member result = memberService.join(regForm);
        //then
        assertThat(result.getEmail()).isEqualTo(regForm.getEmail());
        assertThat(result.getMemberName()).isEqualTo(regForm.getMemberName());
        assertThat(result.getNickname()).isEqualTo(regForm.getNickname());
        assertThat(result.getUserPassword()).isEqualTo(passwordEncoder.encode(regForm.getPassword()));

    }

    @Test
    @DisplayName("카테고리 미지정 회원 정보 변경  테스트")
    public void updateMemberInfo() throws Exception {
        //given
        Member member = createMember();
        Category category = createCategory("category");
        when(memberRepository.findByEmail(any())).thenReturn(Optional.of(member));
        when(categoryRepository.findByCategoryName(any())).thenReturn(Optional.of(category));
        MemberInterest interest = createMemberInterest(member, category);
        List<String> interests = new ArrayList<>();
        interests.add("category");
        MemberInfo changeForm = MemberInfo.builder()
                .email("test@email.com")
                .memberName("tester")
                .nickname("changeNick")
                .link("test link")
                .interests(interests)
                .build();

        //when
        memberService.updateMemberInfo(changeForm);

        //then
        assertThat(member.getNickname()).isEqualTo(changeForm.getNickname());
        assertThat(member.getLink()).isNotNull();
        verify(memberInterestRepository, times(1)).save(interest);
    }

    @Test
    @DisplayName("카테고리 지정 회원 정보 변경 테스트")
    public void updateMemberInfoIncludeInterest() throws Exception {
        //given
        Member member = createMember();

        when(memberRepository.findByEmail(any())).thenReturn(Optional.of(member));

        List<MemberInterest> originInterest = new ArrayList<>();
        MemberInterest interest = createMemberInterest(member, createCategory("previousCategory"));
        originInterest.add(interest);

        when(memberInterestRepository.findInterests(any())).thenReturn(originInterest);
        when(categoryRepository.findByCategoryName(any())).thenReturn(Optional.of(Category.builder().categoryName("changeCategory").build()));

        List<String> changeInterest = new ArrayList<>();
        changeInterest.add("changeCategory");
        MemberInfo changeForm = MemberInfo.builder()
                .email("test@email.com")
                .memberName("tester")
                .nickname("changeNick")
                .interests(changeInterest)
                .link("test link")
                .build();

        //when
        memberService.updateMemberInfo(changeForm);

        //then
        assertThat(member.getNickname()).isEqualTo(changeForm.getNickname());
        assertThat(member.getLink()).isNotNull();
        assertThat(originInterest.get(0).getCategory().getCategoryName()).isEqualTo("changeCategory");
    }

    private MemberInterest createMemberInterest(Member member, Category previousCategory) {
        return MemberInterest.builder().member(member).category(previousCategory).build();
    }

    private Member createMember() {
        return Member.builder()
                .email("test@email.com")
                .memberName("tester")
                .nickname("testNick")
                .userPassword("1234").build();
    }

    private Category createCategory(String category) {
        return Category.builder().categoryName(category).build();
    }




}
