package com.matching.mentoree.service.dto;

import com.matching.mentoree.domain.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class ProgramDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ProgramCreateDTO {

        private String programName;
        private int targetNumber;
        private String goal;
        private String description;
        private String category;
        private Boolean mentor;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dueDate;
        private String programRole; // 멘토여부에 따라서 멘토 아니면 전부 멘티

        @Builder
        public ProgramCreateDTO(String programName, int targetNumber, String goal
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
    public static class ProgramJoinDTO {
        private String email;
        private String msg;
        private String role;

        @Builder
        public ProgramJoinDTO(String email, String msg, String role) {
            this.email = email;
            this.msg = msg;
            this.role = role;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString(exclude = "mentor")
    public static class ProgramInfoDTO {

        private Long id;
        private String title;
        private String category;
        private int maxMember;
        private List<Participant> mentor = new ArrayList<>();
        private String goal;
        private String description;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dueDate;

        @Builder
        public ProgramInfoDTO(Long id, String title, String category, int maxMember, List<Participant> mentor, String description, LocalDate dueDate) {
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
    public static class ProgramBrowseDTO {

        private String title;
        private Long programId;
        private List<MissionDTO> curMission = new ArrayList<>();
        private List<MissionDTO> endMission = new ArrayList<>();

        @Builder
        public ProgramBrowseDTO(String title, Long programId, List<MissionDTO> curMission, List<MissionDTO> endMission) {
            this.title = title;
            this.programId = programId;
            this.curMission = curMission;
            this.endMission = endMission;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ProgramForNavbarDTO {

        private Long id;
        private String title;

        @Builder
        public ProgramForNavbarDTO(Long id, String title) {
            this.id = id;
            this.title = title;
        }

    }


}
