package com.matching.mentoree.service;

import com.matching.mentoree.domain.*;
import com.matching.mentoree.exception.NoAuthorityException;
import com.matching.mentoree.repository.MissionRepository;
import com.matching.mentoree.repository.ParticipantRepository;
import com.matching.mentoree.service.dto.MissionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

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
    private ParticipantRepository participantRepository;

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
                .password("1234")
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
                .period(3)
                .program(testProgram)
                .title("test title")
                .build();

        given(missionRepository.save(argThat(mission -> mission.getTitle().equals("test title"))))
                .willReturn(missionDTO.toEntity());
        given(participantRepository.findByMember(any())).willReturn(Optional.of(participant));

        //when
        missionService.createMission(missionDTO, testMember);
        //then
        verify(missionRepository).save(argThat(mission -> mission.getTitle().equals("test title")));
    }

    @Test
    @DisplayName("미션 작성 미권한 예외")
    public void create_mission_test_exception() throws Exception {
        MissionDTO missionDTO = MissionDTO.builder()
                .content("test mission")
                .period(3)
                .program(testProgram)
                .title("test title")
                .build();

        Participant unAuthParticipant = Participant.builder()
                .approval(true)
                .role(ProgramRole.MENTEE)
                .program(testProgram)
                .member(testMember)
                .isHost(false)
                .build();

        given(missionRepository.save(argThat(mission -> mission.getTitle().equals("test title"))))
                .willReturn(missionDTO.toEntity());
        given(participantRepository.findByMember(any())).willReturn(Optional.of(unAuthParticipant));

        //when
        //then
        assertThrows(NoAuthorityException.class, () ->
                missionService.createMission(missionDTO, testMember));
    }

}