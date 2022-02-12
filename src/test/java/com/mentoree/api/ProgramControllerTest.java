package com.mentoree.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.category.domain.Category;
import com.mentoree.config.WebConfig;
import com.mentoree.config.WebSecurityConfig;
import com.mentoree.config.security.JwtFilter;
import com.mentoree.member.domain.Member;
import com.mentoree.member.repository.MemberRepository;
import com.mentoree.participants.domain.Participant;
import com.mentoree.participants.repository.ParticipantRepository;
import com.mentoree.program.api.ProgramAPIController;
import com.mentoree.program.api.dto.ProgramDTO.ProgramCreateDTO;
import com.mentoree.program.domain.Program;
import com.mentoree.program.domain.ProgramRole;
import com.mentoree.program.repository.ProgramRepository;
import com.mentoree.program.service.ProgramService;
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
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.mentoree.participants.api.dto.ParticipantDTOCollection.*;
import static com.mentoree.program.api.dto.ProgramDTO.*;
import static com.mentoree.program.api.dto.ProgramRequestDTO.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ProgramAPIController.class,
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {WebSecurityConfig.class, WebConfig.class, JwtFilter.class})},
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class,
                OAuth2ClientAutoConfiguration.class, OAuth2ResourceServerAutoConfiguration.class}
)
public class ProgramControllerTest {
    @MockBean
    ProgramRepository programRepository;
    @MockBean
    ProgramService programService;
    @MockBean
    MemberRepository memberRepository;
    @MockBean
    ParticipantRepository participantRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    private Program program;
    private Member member;
    private Category category;
    private ProgramInfoDTO programInfo;
    private ParticipatedProgramDTO participatedProgram;
    private List<ProgramInfoDTO> programInfoList = new ArrayList<>();
    private List<ParticipatedProgramDTO> participateList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("test@email.com", "",
                Collections.singletonList(new SimpleGrantedAuthority("USER")));

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authentication);


        member = Member.builder().email("test@email.com").memberName("tester").nickname("testNick").userPassword("1234").build();
        category = Category.builder().categoryName("testCategory").build();
        program = Program.builder().programName("testProgram").maxMember(5).goal("testGoal").dueDate(LocalDate.now()).description("testDesc").category(category).build();
        programInfo = ProgramInfoDTO.builder().category("category").title("testProgram").description("testDesc").maxMember(5).dueDate(LocalDate.now()).build();
        programInfoList.add(programInfo);
        participatedProgram = ParticipatedProgramDTO.builder().id(1L).title("title").build();
        participateList.add(participatedProgram);
    }

    @Test
    @DisplayName("프로그램 생성 컨트롤러 테스트")
    public void createProgramTest() throws Exception {
        //given
        when(memberRepository.findByEmail(any())).thenReturn(Optional.of(member));
        when(programService.createProgram(any(), any())).thenReturn(program);

        ProgramCreateDTO requestData = ProgramCreateDTO.builder()
                .programRole(ProgramRole.MENTEE.getKey())
                .mentor(false)
                .dueDate(LocalDate.now())
                .programName("testProgram")
                .targetNumber(5)
                .goal("testGoal")
                .description("testDesc")
                .category("testCategory")
                .build();
        String requestBody = objectMapper.writeValueAsString(requestData);
        //when
        ResultActions result = mockMvc.perform(
                post("/api/programs/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("testProgram"));
    }

    @Test
    @DisplayName("프로그램 리스트 불러오기 컨트롤러 테스트")
    public void getMoreListTest() throws Exception {
        //given
        PageRequest page = PageRequest.of(0, 5);
        SliceImpl<ProgramInfoDTO> result = new SliceImpl<>(programInfoList, page, true);
        when(programRepository.findAllProgram(any(),any())).thenReturn(result);

        ProgramRequest programRequest = new ProgramRequest();
        programRequest.setPage(0);
        programRequest.setParticipatedPrograms(participateList);
        String requestBody = objectMapper.writeValueAsString(programRequest);
        //when
        ResultActions response = mockMvc.perform(
                post("/api/programs/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody));
        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.programList").exists())
                .andExpect(jsonPath("$.programList[0].title").value("testProgram"))
                .andExpect(jsonPath("$.programList[0].description").value("testDesc"))
                .andExpect(jsonPath("$.hasNext").value(true));


    }


    @Test
    @DisplayName("추천 프로그램 리스트 불러오기 컨트롤러 테스트")
    public void getMoreRecommendListTest() throws Exception {
        //given
        PageRequest page = PageRequest.of(0, 1);
        SliceImpl<ProgramInfoDTO> sliceProgramInfo = new SliceImpl<>(programInfoList, page, true);
        when(programRepository.findRecommendPrograms(any(),any(),any())).thenReturn(sliceProgramInfo);

        List<String> interests = new ArrayList<>();
        interests.add("testCategory");

        RecommendProgramRequest recommendProgramRequest = new RecommendProgramRequest();
        recommendProgramRequest.setInterests(interests);
        recommendProgramRequest.setParticipatedPrograms(participateList);
        recommendProgramRequest.setPage(0);
        String requestBody = objectMapper.writeValueAsString(recommendProgramRequest);
        //when
        ResultActions response = mockMvc.perform(
                post("/api/programs/list/recommend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );
        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.programRecommendList").exists())
                .andExpect(jsonPath("$.programRecommendList[0].title").value("testProgram"))
                .andExpect(jsonPath("$.programRecommendList[0].description").value("testDesc"))
                .andExpect(jsonPath("$.hasNext").value(true));
    }

    @Test
    @DisplayName("프로그램 정보 받기 테스트 컨트롤러 테스트")
    public void programInfoGetTest() throws Exception {
        //given
        Participant host = Participant.builder().program(program).isHost(true).member(member).approval(true).role(ProgramRole.MENTOR).build();
        when(participantRepository.findHost(any())).thenReturn(Optional.of(host));
        when(programRepository.findProgramById(any())).thenReturn(Optional.of(programInfo));

        //when
        ResultActions response = mockMvc.perform(
                get("/api/programs/1")
        );
        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.programInfo").exists())
                .andExpect(jsonPath("$.programInfo.title").value(programInfo.getTitle()))
                .andExpect(jsonPath("$.isHost").value(true));
    }

    @Test
    @DisplayName("프로그램 참가 첫 신청 컨트롤러 테스트")
    public void applyProgramTest() throws Exception {
        //given
        ApplyRequest applyRequest = new ApplyRequest();
        applyRequest.setProgramId(1L);
        applyRequest.setMessage("applyMessage");
        applyRequest.setNickname(member.getNickname());
        applyRequest.setRole(ProgramRole.MENTOR);
        String requestBody = objectMapper.writeValueAsString(applyRequest);

        when(memberRepository.findByEmail(any())).thenReturn(Optional.of(member));
        when(programService.applyProgram(any(), any(), any(), any())).thenReturn(true);
        //when
        ResultActions response = mockMvc.perform(
                post("/api/programs/1/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );
        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"));
    }

    @Test
    @DisplayName("프로그램 참가 중복 신청 컨트롤러 테스트")
    public void applyProgramMoreThanTwiceTest() throws Exception {
        //given
        ApplyRequest applyRequest = createApplyRequest(1L, "message", "testNick", ProgramRole.MENTEE);
        String requestBody = objectMapper.writeValueAsString(applyRequest);

        when(memberRepository.findByEmail(any())).thenReturn(Optional.of(member));
        when(programService.applyProgram(any(), any(), any(), any())).thenReturn(false);
        //when
        ResultActions response = mockMvc.perform(
                post("/api/programs/1/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );
        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("duplicate"));
    }

    @Test
    @DisplayName("프로그램 참가자 신청 리스트 컨트롤러 테스트")
    public void programApplicationListGetTest() throws Exception {
        //given
        List<ApplyRequest> applyRequestList = new ArrayList<>();
        applyRequestList.add(createApplyRequest(1L, "message", "testNick", ProgramRole.MENTEE));
        applyRequestList.add(createApplyRequest(2L, "message2", "testNick2", ProgramRole.MENTEE));
        applyRequestList.add(createApplyRequest(3L, "message3", "testNick3", ProgramRole.MENTEE));

        when(programRepository.findProgramById(any())).thenReturn(Optional.of(programInfo));
        when(participantRepository.findAllApplicants(any())).thenReturn(applyRequestList);
        when(participantRepository.countCurrentMember(any())).thenReturn(5L);
        when(participantRepository.isHost(any(), any())).thenReturn(true);
        //when
        ResultActions response = mockMvc.perform(
                get("/api/programs/1/applicants")
        );
        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.programInfo").exists())
                .andExpect(jsonPath("$.applicants").exists())
                .andExpect(jsonPath("$.currentNumMember").exists());
    }

    @Test
    @DisplayName("프로그램 참가 승인 컨트롤러 테스트")
    public void applicantTest() throws Exception {
        //given
        when(participantRepository.isHost(any(), any())).thenReturn(true);
        //when
        ResultActions response = mockMvc.perform(
                post("/api/programs/1/applicants/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("1")
        );
        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"));
        verify(programService, times(1)).approval(any(), any());
    }

    @Test
    @DisplayName("프로그램 참가 거절 컨트롤러 테스트")
    public void applicantRejectTest() throws Exception {
        //given
        when(participantRepository.isHost(any(), any())).thenReturn(true);
        //when
        ResultActions response = mockMvc.perform(
                post("/api/programs/1/applicants/reject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("1")
        );
        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"));
        verify(programService, times(1)).reject(any(), any());
    }

    private ApplyRequest createApplyRequest(Long id, String message, String nickname, ProgramRole role) {
        ApplyRequest result = new ApplyRequest();
        result.setProgramId(id);
        result.setMessage(message);
        result.setNickname(nickname);
        result.setRole(role);
        return result;
    }

}
