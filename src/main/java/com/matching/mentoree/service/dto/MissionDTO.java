package com.matching.mentoree.service.dto;

import com.matching.mentoree.domain.Mission;
import com.matching.mentoree.domain.Program;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
public class MissionDTO {

    private Long programId;
    private Long missionId;
    private String title;
    private String goal;
    private String content;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    @Builder
    public MissionDTO(Long programId, Long missionId, String title, String content, String goal, LocalDate dueDate) {
        this.programId = programId;
        this.missionId = missionId;
        this.title = title;
        this.content = content;
        this.goal = goal;
        this.dueDate = dueDate;
    }

    public Mission toEntity(Program program) {
        return Mission.builder()
                .program(program)
                .title(title)
                .content(content)
                .goal(goal)
                .dueDate(dueDate)
                .build();
    }


}
