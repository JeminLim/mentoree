package com.mentoree.service;

import com.mentoree.category.domain.Category;
import com.mentoree.category.repository.CategoryRepository;
import com.mentoree.member.domain.Member;
import com.mentoree.participants.domain.Participant;
import com.mentoree.participants.repository.ParticipantRepository;
import com.mentoree.program.api.dto.ProgramDTO;
import com.mentoree.program.api.dto.ProgramDTO.ProgramCreateDTO;
import com.mentoree.program.domain.Program;
import com.mentoree.program.domain.ProgramRole;
import com.mentoree.program.repository.ProgramRepository;
import com.mentoree.program.service.ProgramService;
import org.apache.tomcat.jni.Local;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static com.mentoree.program.api.dto.ProgramDTO.ProgramCreateDTO.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProgramServiceTest {

    @Mock
    private ProgramRepository programRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ParticipantRepository participantRepository;

    @InjectMocks
    private ProgramService programService;

    @Test
    @DisplayName("프로그램 생성 성공")
    public void createProgram_success() throws Exception {
        //given
        ProgramCreateDTO createForm = builder()
                .category("category")
                .description("description")
                .programName("programName")
                .programRole("MENTEE")
                .dueDate(LocalDate.now())
                .goal("goal")
                .mentor(false)
                .targetNumber(5)
                .build();
        Category category = createCategory();
        Member member = createMember();

        when(categoryRepository.findByCategoryName(any())).thenReturn(Optional.of(category));
        when(programRepository.save(any())).thenReturn(createForm.toEntity(category));
        //when
        Program result = programService.createProgram(createForm, member);
        //then
        assertThat(result.getProgramName()).isEqualTo(createForm.getProgramName());
        assertThat(result.getDescription()).isEqualTo(createForm.getDescription());
        assertThat(result.getDueDate()).isEqualTo(createForm.getDueDate());
        assertThat(result.getMaxMember()).isEqualTo(createForm.getTargetNumber());
    }

    private Category createCategory() {
        return Category.builder().categoryName("category").build();
    }

    @Test
    @DisplayName("프로그램 수정 성공")
    public void updateProgramInfo() throws Exception {
        //given
        Program program = createProgram();

        when(programRepository.findById(any())).thenReturn(Optional.of(program));

        ProgramCreateDTO updateForm = builder()
                .programName("changedTitle")
                .description("changedDesc")
                .targetNumber(4)
                .goal("changedGoal")
                .dueDate(LocalDate.now().plusDays(7))
                .build();
        //when
        programService.updateProgramInfo(updateForm, 1L);
        //then

        assertThat(program.getProgramName()).isEqualTo(updateForm.getProgramName());
        assertThat(program.getDescription()).isEqualTo(updateForm.getDescription());
        assertThat(program.getMaxMember()).isEqualTo(updateForm.getTargetNumber());
        assertThat(program.getGoal()).isEqualTo(updateForm.getGoal());
        assertThat(program.getDueDate()).isEqualTo(updateForm.getDueDate());
    }

    @Test
    @DisplayName("프로그램 참여 승인")
    public void participantApprove() throws Exception {
        //given
        Member member = createMember();
        Program program = createProgram();
        Participant participant = createParticipant(member, program);
        when(participantRepository.findParticipantByMemberIdAndProgram(any(), any())).thenReturn(Optional.of(participant));
        //when
        programService.approval(1L, 2L);
        //then
        assertThat(participant.isApproval()).isTrue();
    }

    @Test
    @DisplayName("프로그램 참가 승인 거절")
    public void participantReject() throws Exception {
        //given
        Member member = createMember();
        Program program = createProgram();
        Participant participant = createParticipant(member, program);
        when(participantRepository.findParticipantByMemberIdAndProgram(any(), any())).thenReturn(Optional.of(participant));
        //when
        programService.reject(1L, 2L);
        //then
        verify(participantRepository).delete(participant);
    }

    private Participant createParticipant(Member member, Program program) {
        return Participant.builder()
                .member(member)
                .program(program)
                .role(ProgramRole.MENTEE)
                .isHost(false)
                .build();
    }

    private Program createProgram() {
        return Program.builder()
                .category(createCategory())
                .maxMember(5)
                .goal("goal")
                .dueDate(LocalDate.now().plusDays(5))
                .description("decription")
                .programName("title")
                .build();
    }

    private Member createMember() {
        return Member.builder()
                .email("test@email.com")
                .memberName("tester")
                .nickname("testernick")
                .userPassword("1234")
                .build();
    }


}
