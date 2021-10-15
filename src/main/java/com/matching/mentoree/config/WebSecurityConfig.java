package com.matching.mentoree.config;

import com.matching.mentoree.config.security.*;
import com.matching.mentoree.config.security.util.CookieUtil;
import com.matching.mentoree.config.security.util.JwtUtils;
import com.matching.mentoree.repository.ParticipantRepository;
import com.matching.mentoree.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailService customUserDetailService;
    private final JwtUtils jwtUtils;
    private final CookieUtil cookieUtil;
    private final TokenRepository tokenRepository;
    private final ParticipantRepository participantRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/login", "/member/**").permitAll()
                .anyRequest().authenticated();

        http
                .formLogin()
                .successHandler(new CustomAuthenticationSuccessHandler(cookieUtil, jwtUtils, tokenRepository, participantRepository))
                .failureHandler(new CustomAuthenticationFailureHandler())
                .loginPage("/login")
                .loginProcessingUrl("/login/process")
                .permitAll();

        http
                .oauth2Login()
                .loginPage("/login")
                .successHandler(new CustomAuthenticationSuccessHandler(cookieUtil, jwtUtils, tokenRepository, participantRepository))
                .failureHandler(new CustomAuthenticationFailureHandler())
                .userInfoEndpoint()
                .userService(customUserDetailService);


        http
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .deleteCookies("AccessToken");

        http
                .addFilterAfter(new JwtFilter(cookieUtil, jwtUtils), LogoutFilter.class);

    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.authenticationProvider(new CustomAuthenticationProvider(passwordEncoder(), customUserDetailService));
        auth.userDetailsService(customUserDetailService).passwordEncoder(passwordEncoder());
    }


}
