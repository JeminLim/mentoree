package com.matching.mentoree.service.dto;

import com.matching.mentoree.domain.Mission;
import com.matching.mentoree.domain.Program;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class MissionDTO {

    private Program program;
    private String title;
    private String content;
    private int period;

    @Builder
    public MissionDTO(Program program, String title, String content, int period) {
        this.program = program;
        this.title = title;
        this.content = content;
        this.period = period;
    }

    public Mission toEntity() {
        LocalDateTime dueDate = LocalDateTime.now().plusDays(period);
        return Mission.builder()
                .program(program)
                .title(title)
                .content(content)
                .dueDate(dueDate)
                .build();
    }


}
