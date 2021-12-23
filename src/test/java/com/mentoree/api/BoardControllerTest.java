package com.mentoree.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.board.api.BoardAPIController;
import com.mentoree.board.api.dto.BoardDTO;
import com.mentoree.board.repository.BoardRepository;
import com.mentoree.board.service.BoardService;
import com.mentoree.config.WebConfig;
import com.mentoree.config.WebSecurityConfig;
import com.mentoree.config.security.JwtFilter;
import com.mentoree.member.domain.Member;
import com.mentoree.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;
import java.util.Optional;

import static com.mentoree.board.api.dto.BoardDTO.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = BoardAPIController.class,
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {WebSecurityConfig.class, WebConfig.class, JwtFilter.class})},
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class,
                OAuth2ClientAutoConfiguration.class, OAuth2ResourceServerAutoConfiguration.class}
)
public class BoardControllerTest {

    @MockBean
    BoardService boardService;
    @MockBean
    BoardRepository boardRepository;
    @MockBean
    MemberRepository memberRepository;

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;

    private Member member;
    private BoardInfo boardInfo;
    @BeforeEach
    void setUp() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("test@email.com", "",
                Collections.singletonList(new SimpleGrantedAuthority("USER")));

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authentication);

        member = Member.builder().email("test@email.com").memberName("tester").nickname("testNick").userPassword("1234").build();
        boardInfo = BoardInfo.builder().content("content").missionId(1L).missionTitle("missionTitle").writerNickname("testNick").build();
    }

    @Test
    @DisplayName("수행보드 정보 가져오기 컨트롤러 테스트")
    public void getBoardInfoTest() throws Exception {
        //given
        when(boardRepository.findBoardInfoById(any())).thenReturn(Optional.of(boardInfo));
        //when
        ResultActions response = mockMvc.perform(get("/api/board/1/info"));
        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.boardInfo").exists());
    }

    @Test
    @DisplayName("수행보드 작성 컨트롤러 테스트")
    public void createBoardTest() throws Exception {
        //given
        when(memberRepository.findByEmail(any())).thenReturn(Optional.of(member));
        String requestBody = objectMapper.writeValueAsString(boardInfo);
        //when
        ResultActions response = mockMvc.perform(
                post("/api/board")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );
        //then
        response.andExpect(status().isOk())
                .andExpect(content().string("success"));
        verify(boardService, times(1)).saveBoard(any(), any());
    }

}
