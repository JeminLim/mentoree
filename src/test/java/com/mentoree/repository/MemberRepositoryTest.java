package com.mentoree.repository;

import com.mentoree.category.domain.Category;
import com.mentoree.category.repository.CategoryRepository;
import com.mentoree.member.api.dto.MemberDTO;
import com.mentoree.member.domain.Member;
import com.mentoree.member.domain.MemberInterest;
import com.mentoree.member.repository.MemberInterestRepository;
import com.mentoree.member.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.event.annotation.AfterTestMethod;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static com.mentoree.member.api.dto.MemberDTO.*;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberInterestRepository memberInterestRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    private Member testMember;
    private MemberInterest memberInterest1;
    private MemberInterest memberInterest2;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .email("test@email.com")
                .memberName("testMemberName")
                .nickname("testNickname")
                .userPassword("1234")
                .build();

        memberRepository.save(testMember);

        Category testCategory1 = categoryRepository.save(Category.builder().categoryName("testCategory1").build());
        Category testCategory2 = categoryRepository.save(Category.builder().categoryName("testCategory2").build());

        memberInterest1 = MemberInterest.builder().member(testMember).category(testCategory1).build();
        memberInterest2 = MemberInterest.builder().member(testMember).category(testCategory2).build();

        memberInterestRepository.save(memberInterest1);
        memberInterestRepository.save(memberInterest2);
    }

    @Test
    @DisplayName("이메일 no 중복")
    void noDuplicateEmail() {
        assertThat(memberRepository.existsByEmail("test222@email.com")).isFalse();
    }

    @Test
    @DisplayName("이메일 중복")
    void duplicateEmail() {
        assertThat(memberRepository.existsByEmail(testMember.getEmail())).isTrue();
    }

    @Test
    @DisplayName("닉네임 no 중복")
    void noDuplicateNickname() {
        assertThat(memberRepository.existsByNickname("testNickname222")).isFalse();
    }

    @Test
    @DisplayName("닉네임 중복")
    void duplicateNickname() {
        assertThat(memberRepository.existsByNickname(testMember.getNickname())).isTrue();
    }

    @Test
    @DisplayName("이메일로 회원 정보 얻어오기")
    void getMemberInfoByEmail() {

        MemberInfo findMember = memberRepository.findMemberInfoByEmail("test@email.com").orElseThrow(NoSuchElementException::new);

        assertThat(findMember.getEmail()).isEqualTo(testMember.getEmail());
        assertThat(findMember.getMemberName()).isEqualTo(testMember.getMemberName());
        assertThat(findMember.getNickname()).isEqualTo(testMember.getNickname());
        assertThat(findMember.getInterests().size()).isEqualTo(2);
        assertThat(findMember.getInterests().get(0)).isEqualTo("testCategory1");
    }

    @Test
    @DisplayName("유저 관심사 가져오기")
    void getInterests() {
        List<MemberInterest> interests = memberInterestRepository.findInterests(testMember.getId());
        assertThat(interests.size()).isEqualTo(2);
        assertThat(interests).contains(memberInterest1, memberInterest2);
    }


}
