package com.matching.mentoree.service.dto;

import com.matching.mentoree.domain.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProgramCreateDTO {

    private String programName;
    private Member host;
    private int targetNumber;
    private String goal;
    private String description;
    private String programRole;
    private List<String> categories;

    @Builder
    public ProgramCreateDTO(String programName, Member host, int targetNumber, String goal, String description, String programRole, List<String> categories) {
        this.programName = programName;
        this.host = host;
        this.targetNumber = targetNumber;
        this.goal = goal;
        this.description = description;
        this.programRole = programRole;
        this.categories = categories;
    }

    public Program toProgramEntity() {
        return Program.builder()
                .programName(programName)
                .description(description)
                .maxMember(targetNumber)
                .goal(goal)
                .build();
    }
}
