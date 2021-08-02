package com.matching.mentoree.repository;

import com.matching.mentoree.domain.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberRepositoryTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private MemberRepository memberRepository;


    @Test
    public void duplicate_email_check_success() throws Exception {
        //given
        Member member = Member.builder()
                    .username("tester")
                    .email("abc@email.com")
                    .password("1234")
                    .build();
        memberRepository.save(member);

        em.flush();
        em.clear();

        //when
        boolean isDuplicate = memberRepository.existsByEmail(member.getEmail());

        //then
        assertThat(isDuplicate).isTrue();
    }


}