package com.matching.mentoree.service.dto;

import com.matching.mentoree.domain.Mission;
import com.matching.mentoree.domain.Program;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
public class MissionDTO {

    private Long programId;
    private String title;
    private String goal;
    private String content;
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private LocalDateTime dueDate;

    @Builder
    public MissionDTO(Long programId, String title, String content, String goal, LocalDateTime dueDate) {
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
                .content(content)
                .dueDate(dueDate)
                .build();
    }


}
