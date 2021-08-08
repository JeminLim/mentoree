package com.matching.mentoree.service;

import com.matching.mentoree.domain.*;
import com.matching.mentoree.repository.*;
import com.matching.mentoree.service.dto.ProgramDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(SpringExtension.class)
class ProgramServiceTest {

    @Mock
    private ProgramRepository programRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProgramCategoryRepository programCategoryRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ParticipantRepository participantRepository;

    @InjectMocks
    private ProgramService programService;

    private ProgramDTO programDTO;
    private Member member;

    @BeforeEach
    public void setUp() {
        List<String> categories = new ArrayList<>();
        categories.add("test");

        programDTO = ProgramDTO.builder()
                .programName("test program")
                .targetNumber(5)
                .description("awesome program")
                .goal("test goal")
                .categories(categories)
                .programRole("MENTOR")
                .build();

        member = Member.builder().memberName("tester").userPassword("1234").email("test@email.com").nickname("testNick").build();
    }

    @Test
    public void create_program_test() throws Exception {
        //given
        given(programRepository.save(argThat(program -> program.getProgramName().equals("test program"))))
                .willReturn(Program.builder()
                        .programName(programDTO.getProgramName())
                        .maxMember(programDTO.getTargetNumber())
                        .description(programDTO.getDescription())
                        .goal(programDTO.getGoal())
                        .build()
                );

        given(categoryRepository.save(argThat(category -> category.getCategoryName().equals("test"))))
                .willReturn(Category.builder().categoryName("test").build());

        given(programCategoryRepository.save(argThat(pc -> pc.getProgram().equals(programDTO.toEntity()))))
                .willReturn(ProgramCategory.builder()
                        .program(programDTO.toEntity())
                        .category(Category.builder().categoryName("test").build()));

        given(memberRepository.findById(any())).willReturn(Optional.of(Member.builder().memberName("tester").userPassword("1234").email("test@email.com").build()));

        given(participantRepository.save(argThat(participant -> participant.isHost() == true)))
                .willReturn(Participant.builder()
                        .isHost(true)
                        .member(member)
                        .program(programDTO.toEntity())
                        .role(ProgramRole.MENTEE)
                        .build());

        //when
        programService.createProgram(programDTO, member);

        //then
        verify(programRepository).save(argThat(program -> program.getProgramName().equals("test program")));
        verify(categoryRepository).save(argThat(category -> category.getCategoryName().equals("test")));
        verify(programCategoryRepository).save(argThat(pc -> pc.getProgram().getProgramName().equals("test program")));
        verify(participantRepository).save(argThat(m -> m.isHost() == true));
    }

    @Test
    @DisplayName("프로그램 수정 테스트 성공")
    public void update_program_info_test() throws Exception {
        //given
        ProgramDTO newDto = ProgramDTO.builder()
                .programName("changedName")
                .description("changedDesc")
                .targetNumber(22)
                .goal("changedGoal")
                .build();

        Program program = programDTO.toEntity();

        given(programRepository.findById(any())).willReturn(Optional.of(program));

        //when
        programService.updateProgramInfo(newDto, 1L);

        //then
        verify(programRepository, times(1)).findById(any());
        assertThat(program.getProgramName()).isEqualTo(newDto.getProgramName());
        assertThat(program.getDescription()).isEqualTo(newDto.getDescription());
        assertThat(program.getMaxMember()).isEqualTo(newDto.getTargetNumber());
        assertThat(program.getGoal()).isEqualTo(newDto.getGoal());
    }

    @Test
    @DisplayName("프로그램 참여 신청 성공")
    public void apply_program_test() throws Exception {
        //given
        given(programRepository.findById(any())).willReturn(Optional.of(programDTO.toEntity()));

        //when
        programService.applyProgram(member, "MENTEE", 1L);
        //then
        verify(participantRepository).save(argThat(participant -> participant.getProgram().equals(programDTO.toEntity())));
    }

    @Test
    @DisplayName("참가 신청 승인 테스트 성공")
    public void participant_approval_test() throws Exception {
        //given
        Participant participant = Participant.builder()
                .approval(false)
                .role(ProgramRole.MENTEE)
                .program(programDTO.toEntity())
                .member(member)
                .isHost(false)
                .build();
        given(memberRepository.findById(any())).willReturn(Optional.of(member));
        given(programRepository.findById(any())).willReturn(Optional.of(programDTO.toEntity()));
        given(participantRepository.findByMemberAndProgram(member, programDTO.toEntity())).willReturn(Optional.of(participant));

        //when
        programService.approval(1L, 1L);
        //then
        assertThat(participant.isApproval()).isTrue();
    }

    @Test
    @DisplayName("참가 신청 거부 테스트 성공")
    public void participant_reject_test() throws Exception {
        //given
        Participant participant = Participant.builder()
                .approval(false)
                .role(ProgramRole.MENTEE)
                .program(programDTO.toEntity())
                .member(member)
                .isHost(false)
                .build();
        given(memberRepository.findById(any())).willReturn(Optional.of(member));
        given(programRepository.findById(any())).willReturn(Optional.of(programDTO.toEntity()));
        given(participantRepository.findByMemberAndProgram(member, programDTO.toEntity())).willReturn(Optional.of(participant));
        //when
        programService.reject(1L, 1L);
        //then
        verify(participantRepository, times(1)).delete(participant);
    }

}