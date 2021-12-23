package com.mentoree.mission.api.dto;

import com.mentoree.mission.domain.Mission;
import com.mentoree.program.domain.Program;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
public class MissionDTO {

    private Long id;
    private String title;
    private String goal;
    private String content;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    @Builder
    public MissionDTO(Long id, String title, String content, String goal, LocalDate dueDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.goal = goal;
        this.dueDate = dueDate;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class MissionCreateRequest {

        private Long programId;
        private String title;
        private String goal;
        private String content;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dueDate;

        @Builder
        public MissionCreateRequest(Long programId, String title, String content, String goal, LocalDate dueDate) {
            this.programId = programId;
            this.title = title;
            this.content = content;
            this.goal = goal;
            this.dueDate = dueDate;
        }

        public Mission toEntity(Program program) {
            return Mission.builder()
                    .program(program)
                    .title(title)
                    .goal(goal)
                    .content(content)
                    .dueDate(dueDate)
                    .build();
        }

    }

}
