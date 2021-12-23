package com.mentoree.service;

import com.mentoree.category.domain.Category;
import com.mentoree.mission.api.dto.MissionDTO;
import com.mentoree.mission.domain.Mission;
import com.mentoree.mission.repository.MissionRepository;
import com.mentoree.mission.service.MissionService;
import com.mentoree.program.domain.Program;
import com.mentoree.program.repository.ProgramRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static com.mentoree.mission.api.dto.MissionDTO.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MissionServiceTest {

    @Mock
    private MissionRepository missionRepository;
    @Mock
    private ProgramRepository programRepository;
    
    @InjectMocks
    MissionService missionService;
    
    @Test
    @DisplayName("미션 생성 테스트")
    public void createMission() throws Exception {
        //given
        Program program = createProgram();
        when(programRepository.findById(any())).thenReturn(Optional.of(program));
        MissionCreateRequest createForm = MissionCreateRequest.builder()
                .content("missionContent")
                .title("missionTitle")
                .programId(1L)
                .dueDate(LocalDate.now())
                .build();
        //when
        missionService.createMission(createForm);
        //then
        verify(missionRepository, times(1)).save(createForm.toEntity(program));
    }

    @Test
    @DisplayName("미션 수정 테스트")
    public void updateMissionTEst() throws Exception {
        //given
        Program program = createProgram();
        Mission mission = createMission(program);
        when(missionRepository.findById(any())).thenReturn(Optional.of(mission));
        MissionDTO updateForm = builder()
                .id(1L)
                .title("changeTitle")
                .content("changeContent")
                .dueDate(LocalDate.now().plusDays(2))
                .goal("goal")
                .build();
        //when
        Mission result = missionService.updateMission(updateForm);

        //then
        assertThat(result.getTitle()).isEqualTo(updateForm.getTitle());
        assertThat(result.getContent()).isEqualTo(updateForm.getContent());
        assertThat(result.getDueDate()).isEqualTo(updateForm.getDueDate());
    }

    private Mission createMission(Program program) {
        return Mission.builder()
                .program(program)
                .title("title")
                .goal("goal")
                .dueDate(LocalDate.now())
                .content("content")
                .build();
    }

    private Program createProgram() {
        return Program.builder()
                .programName("title")
                .description("desc")
                .dueDate(LocalDate.now())
                .goal("goal")
                .maxMember(5)
                .category(Category.builder().categoryName("category").build())
                .build();
    }


}
