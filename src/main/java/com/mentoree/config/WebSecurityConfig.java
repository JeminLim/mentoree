package com.mentoree.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.config.security.CustomAuthenticationEntryPoint;
import com.mentoree.config.security.util.JwtUtils;
import com.mentoree.participants.repository.ParticipantRepository;
import com.mentoree.global.repository.TokenRepository;
import com.mentoree.config.security.CustomAuthenticationProvider;
import com.mentoree.config.security.CustomUserDetailService;
import com.mentoree.config.security.JwtFilter;
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
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailService customUserDetailService;
    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;
    private static final String[] NO_AUTH_PATH = {
            "/api/login/**",
            "/api/reissue",
            "/api/members/join",
            "/api/members/join/**",
            "/api/programs/**",
            "/swagger-ui.html/**",
            "/swagger-resources/**",
            "/v2/api-docs",
            "/webjars/**"
    };


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http
                .cors()
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(NO_AUTH_PATH).permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint(objectMapper));

        http
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/api/login/process")
                .successForwardUrl("/api/login/success")
                .failureForwardUrl("/api/login/fail")
                .permitAll();

        http
                .oauth2Login()
                .loginPage("/login")
                .userInfoEndpoint()
                .userService(customUserDetailService)
                .and()
                .defaultSuccessUrl("/api/login/success");


        http
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");

        http
                .addFilterAfter(jwtFilter(), LogoutFilter.class);

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

    //== 빈 등록 ==//
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public JwtFilter jwtFilter() {
        JwtFilter jwtFilter = new JwtFilter(jwtUtils);
        jwtFilter.excludePath(NO_AUTH_PATH);
        return jwtFilter;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("http://localhost:8081");
            }
        };
    }

}
