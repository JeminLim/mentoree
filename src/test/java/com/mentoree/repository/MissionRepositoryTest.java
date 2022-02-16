package com.mentoree.repository;


import com.mentoree.mission.api.dto.MissionDTOCollection;
import com.mentoree.mission.domain.Mission;
import com.mentoree.mission.repository.MissionRepository;
import com.mentoree.program.domain.Program;
import com.mentoree.program.repository.ProgramRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static com.mentoree.mission.api.dto.MissionDTOCollection.*;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MissionRepositoryTest {

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private ProgramRepository programRepository;

    private Mission openMission;
    private Mission closeMission;

    private Program testProgram;

    @BeforeEach
    void setUp() {
        testProgram = Program.builder()
                .programName("testProgram")
                .description("test")
                .dueDate(LocalDate.now().plusDays(5))
                .goal("forTest")
                .maxMember(5)
                .build();
        programRepository.save(testProgram);

        openMission = Mission.builder()
                .program(testProgram)
                .dueDate(LocalDate.now().plusDays(5))
                .goal("testGoal")
                .content("test content")
                .title("testMission")
                .build();

        closeMission = Mission.builder()
                .program(testProgram)
                .dueDate(LocalDate.now().minusDays(5))
                .goal("testGoal")
                .content("test content")
                .title("testMission")
                .build();

        missionRepository.save(openMission);
        missionRepository.save(closeMission);
    }

    @Test
    @DisplayName("아이디로 미션 dto 반환")
    void findMissionById() {
        MissionDTO findMission = missionRepository.findMissionById(openMission.getId()).orElseThrow(NoSuchElementException::new);

        assertThat(findMission.getId()).isEqualTo(openMission.getId());
        assertThat(findMission.getGoal()).isEqualTo(openMission.getGoal());
        assertThat(findMission.getTitle()).isEqualTo(openMission.getTitle());
        assertThat(findMission.getContent()).isEqualTo(openMission.getContent());
        assertThat(findMission.getDueDate()).isEqualTo(openMission.getDueDate());
    }

    @Test
    @DisplayName("현재 진행중인 미션 찾기")
    void findCurrentMission() {
        List<MissionDTO> currentMission = missionRepository.findMissionList(testProgram.getId(), true);

        assertThat(currentMission.size()).isEqualTo(1);
        assertThat(currentMission.get(0).getId()).isEqualTo(openMission.getId());
        assertThat(currentMission.get(0).getGoal()).isEqualTo(openMission.getGoal());
        assertThat(currentMission.get(0).getTitle()).isEqualTo(openMission.getTitle());
        assertThat(currentMission.get(0).getContent()).isEqualTo(openMission.getContent());
        assertThat(currentMission.get(0).getDueDate()).isEqualTo(openMission.getDueDate());
    }


    @Test
    @DisplayName("종료된 미션 찾기")
    void findEndedMission() {
        List<MissionDTO> endedMission = missionRepository.findMissionList(testProgram.getId(), false);

        assertThat(endedMission.size()).isEqualTo(1);
        assertThat(endedMission.get(0).getId()).isEqualTo(closeMission.getId());
        assertThat(endedMission.get(0).getGoal()).isEqualTo(closeMission.getGoal());
        assertThat(endedMission.get(0).getTitle()).isEqualTo(closeMission.getTitle());
        assertThat(endedMission.get(0).getContent()).isEqualTo(closeMission.getContent());
        assertThat(endedMission.get(0).getDueDate()).isEqualTo(closeMission.getDueDate());
    }









}
