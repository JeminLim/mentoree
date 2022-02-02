package com.mentoree.program.api.dto;

import com.mentoree.category.domain.Category;
import com.mentoree.mission.api.dto.MissionDTOCollection;
import com.mentoree.participants.api.dto.ParticipantDTOCollection;
import com.mentoree.program.domain.Program;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.mentoree.participants.api.dto.ParticipantDTOCollection.*;


public class ProgramDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    @ApiModel(description = "프로그램 생성 요청 폼")
    public static class ProgramCreateDTO {

        @NotBlank
        @ApiModelProperty(value = "프로그램 제목")
        private String programName;

        @Range(min = 2, max = 10)
        @ApiModelProperty(value = "프로그램 모집 인원")
        private Integer targetNumber;

        @NotNull
        @ApiModelProperty(value = "프로그램 목표")
        private String goal;

        @NotNull
        @ApiModelProperty(value = "프로그램 설명")
        private String description;

        @NotNull
        @ApiModelProperty(value = "프로그램 분류")
        private String category;

        @NotNull
        @ApiModelProperty(value = "멘토 역할 참가 여부")
        private Boolean mentor;


        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @NotNull
        @ApiModelProperty(value = "프로그램 모집 기한")
        private LocalDate dueDate;

        @ApiModelProperty(hidden = true)
        private String programRole; // 멘토여부에 따라서 멘토 아니면 전부 멘티

        @Builder
        public ProgramCreateDTO(String programName, Integer targetNumber, String goal
                , String description, String programRole, String category, Boolean mentor, LocalDate dueDate) {
            this.programName = programName;
            this.targetNumber = targetNumber;
            this.goal = goal;
            this.description = description;
            this.programRole = programRole;
            this.category = category;
            this.mentor = mentor;
            this.dueDate = dueDate;
        }

        public Program toEntity(Category category) {
            return Program.builder()
                    .programName(programName)
                    .description(description)
                    .maxMember(targetNumber)
                    .goal(goal)
                    .category(category)
                    .dueDate(dueDate)
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString(exclude = "mentor")
    @ApiModel(description = "프로그램 정보")
    public static class ProgramInfoDTO {

        @NotNull
        @ApiModelProperty(value = "프로그램 id")
        private Long id;

        @NotNull
        @ApiModelProperty(value = "프로그램 제목")
        private String title;

        @NotNull
        @ApiModelProperty(value = "프로그램 분류")
        private String category;

        @NotNull
        @ApiModelProperty(value = "프로그램 모집 인원")
        private int maxMember;

        @ApiModelProperty(value = "프로그램 참가 멘토 리스트")
        private List<ParticipantDTO> mentor = new ArrayList<>();

        @NotNull
        @ApiModelProperty(value = "프로그램 목표")
        private String goal;

        @NotNull
        @ApiModelProperty(value = "프로그램 설명")
        private String description;

        @NotNull
        @ApiModelProperty(value = "프로그램 모집기한")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dueDate;

        @Builder
        public ProgramInfoDTO(Long id, String title, String category, int maxMember, List<ParticipantDTO> mentor, String description, LocalDate dueDate) {
            this.id = id;
            this.title = title;
            this.category = category;
            this.maxMember = maxMember;
            this.mentor = mentor;
            this.description = description;
            this.dueDate = dueDate;
        }

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(description = "참가 프로그램 간략 정보")
    public static class ParticipatedProgramDTO {

        @NotNull
        @ApiModelProperty(value = "프로그램 Id")
        private Long id;

        @NotNull
        @ApiModelProperty(value = "프로그램 제목")
        private String title;

        @Builder
        public ParticipatedProgramDTO(Long id, String title) {
            this.id = id;
            this.title = title;
        }

    }


}
