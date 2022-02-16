package com.mentoree.mock;

import com.mentoree.config.security.UserPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Collections;

public class WithCustomMockUserFactory implements WithSecurityContextFactory<WithCustomMockUser> {

    @Override
    public SecurityContext createSecurityContext(WithCustomMockUser annotation) {
        final SecurityContext context = SecurityContextHolder.createEmptyContext();
        UserPrincipal principal = UserPrincipal.builder()
                .email(annotation.username())
                .password(annotation.password())
                .authorities(Collections.singleton(new SimpleGrantedAuthority(annotation.role())))
                .build();
        final UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
        context.setAuthentication(authentication);
        return context;
    }
}
