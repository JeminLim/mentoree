package com.matching.mentoree.service;

import com.matching.mentoree.domain.*;
import com.matching.mentoree.repository.*;
import com.matching.mentoree.service.dto.ProgramCreateDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
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

    @Test
    public void create_program_test() throws Exception {
        //given
        List<String> categories = new ArrayList<>();
        categories.add("test");

        ProgramCreateDTO programCreateDTO = ProgramCreateDTO.builder()
                .programName("test program")
                .targetNumber(5)
                .description("awesome program")
                .goal("test goal")
                .categories(categories)
                .programRole("MENTOR")
                .build();

        given(programRepository.save(argThat(program -> program.getProgramName().equals("test program"))))
                .willReturn(Program.builder()
                        .programName(programCreateDTO.getProgramName())
                        .maxMember(programCreateDTO.getTargetNumber())
                        .description(programCreateDTO.getDescription())
                        .goal(programCreateDTO.getGoal())
                        .build()
                );

        given(categoryRepository.save(argThat(category -> category.getCategoryName().equals("test"))))
                .willReturn(Category.builder().categoryName("test").build());

        given(programCategoryRepository.save(argThat(pc -> pc.getProgram().equals(programCreateDTO.toProgramEntity()))))
                .willReturn(ProgramCategory.builder()
                        .program(programCreateDTO.toProgramEntity())
                        .category(Category.builder().categoryName("test").build()));

        given(memberRepository.findById(any())).willReturn(Optional.of(Member.builder().username("tester").password("1234").email("test@email.com").build()));

        given(participantRepository.save(argThat(participant -> participant.isHost() == true)))
                .willReturn(Participant.builder()
                        .isHost(true)
                        .member(Member.builder().username("tester").password("1234").email("test@email.com").build())
                        .program(programCreateDTO.toProgramEntity())
                        .role(ProgramRole.MENTEE)
                        .build());

        //when
        programService.CreateProgram(programCreateDTO, 1L);

        //then
        verify(programRepository).save(argThat(program -> program.getProgramName().equals("test program")));
        verify(categoryRepository).save(argThat(category -> category.getCategoryName().equals("test")));
        verify(programCategoryRepository).save(argThat(pc -> pc.getProgram().getProgramName().equals("test program")));
        verify(participantRepository).save(argThat(m -> m.isHost() == true));


    }


}