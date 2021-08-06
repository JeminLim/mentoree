package com.matching.mentoree.service;

import com.matching.mentoree.domain.*;
import com.matching.mentoree.repository.*;
import com.matching.mentoree.service.dto.ProgramDTO;
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

        ProgramDTO programDTO = ProgramDTO.builder()
                .programName("test program")
                .targetNumber(5)
                .description("awesome program")
                .goal("test goal")
                .categories(categories)
                .programRole("MENTOR")
                .build();

        Member member = Member.builder().username("tester").userPassword("1234").email("test@email.com").build();

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

        given(memberRepository.findById(any())).willReturn(Optional.of(Member.builder().username("tester").userPassword("1234").email("test@email.com").build()));

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


}