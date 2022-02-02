package com.mentoree.mission.api.dto;

import com.mentoree.mission.domain.Mission;
import com.mentoree.program.domain.Program;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class MissionDTOCollection {

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(description = "미션 정보")
    public static class MissionDTO {

        @NotNull
        @ApiModelProperty(value = "미션 id")
        private Long id;

        @NotNull
        @ApiModelProperty(value = "미션 제목")
        private String title;

        @NotNull
        @ApiModelProperty(value = "미션 목표")
        private String goal;

        @NotNull
        @ApiModelProperty(value = "미션 내용")
        private String content;

        @NotNull
        @ApiModelProperty(value = "미션 마감기한")
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
    }


    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(description = "미션 생성 폼")
    public static class MissionCreateRequest {

        @NotNull
        @ApiModelProperty(value = "프로그램 id")
        private Long programId;

        @NotNull
        @ApiModelProperty(value = "미션 제목")
        private String title;

        @NotNull
        @ApiModelProperty(value = "미션 목표")
        private String goal;

        @NotNull
        @ApiModelProperty(value = "미션 내용")
        private String content;

        @NotNull
        @ApiModelProperty(value = "미션 마감기한")
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
