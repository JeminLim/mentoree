package com.matching.mentoree.service;

import com.matching.mentoree.domain.*;
import com.matching.mentoree.exception.NoAuthorityException;
import com.matching.mentoree.repository.MissionRepository;
import com.matching.mentoree.repository.ProgramRepository;
import com.matching.mentoree.service.dto.MissionDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class MissionServiceTest {

    @Mock
    private MissionRepository missionRepository;
    @Mock
    private ProgramRepository programRepository;

    @InjectMocks
    private MissionService missionService;


    private Program testProgram;
    private Member testMember;
    private Participant participant;

    @BeforeEach
    public void setUp() {
        //given
        testProgram = Program.builder()
                .goal("test goal")
                .maxMember(5)
                .description("test desc")
                .programName("test program")
                .build();

        testMember = Member.builder()
                .username("tester")
                .email("test@email.com")
                .userPassword("1234")
                .nickname("testNick")
                .build();

        participant = Participant.builder()
                .approval(true)
                .role(ProgramRole.MENTOR)
                .program(testProgram)
                .member(testMember)
                .isHost(false)
                .build();
    }

    @Test
    @DisplayName("미션 작성 테스트 성공")
    public void create_mission_test() throws Exception {
        MissionDTO missionDTO = MissionDTO.builder()
                .content("test mission")
                .dueDate(LocalDateTime.now())
                .programId(testProgram.getId())
                .title("test title")
                .build();

        given(missionRepository.save(argThat(mission -> mission.getTitle().equals("test title"))))
                .willReturn(missionDTO.toEntity(testProgram));
        given(programRepository.findById(any())).willReturn(Optional.of(testProgram));

        //when
        missionService.createMission(missionDTO, testMember);
        //then
        verify(missionRepository).save(argThat(mission -> mission.getTitle().equals("test title")));
    }

    @Test
    @DisplayName("미션 카드 변경 성공")
    public void mission_update_test() throws Exception {
        //given
        Mission savedMission = Mission.builder()
                .title("test title")
                .content("test content")
                .program(testProgram)
                .dueDate(LocalDateTime.now().minusDays(5))
                .build();
        MissionDTO missionDTO = MissionDTO.builder()
                .content("changed content")
                .dueDate(LocalDateTime.now().plusDays(5))
                .programId(testProgram.getId())
                .title("test title")
                .build();

        given(missionRepository.findById(any())).willReturn(Optional.of(savedMission));
        //when
        missionService.updateMission(missionDTO, savedMission.getId());

        //then
        assertThat(savedMission.getContent()).isEqualTo(missionDTO.getContent());
        assertThat(savedMission.getDueDate()).isEqualTo(missionDTO.getDueDate());
    }





}