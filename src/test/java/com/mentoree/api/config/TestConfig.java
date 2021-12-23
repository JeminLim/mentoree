package com.mentoree.api.config;

import com.mentoree.config.security.CustomUserDetailService;
import com.mentoree.member.repository.MemberRepository;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;

public class TestConfig {

    @Bean
    public CustomUserDetailService customUserDetailService(MemberRepository memberRepository) {
        return new CustomUserDetailService(memberRepository);
    }

}
