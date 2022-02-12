package com.mentoree.participants.api.dto;

import com.mentoree.program.domain.ProgramRole;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;


public class ParticipantDTOCollection {

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(description = "참가자 정보")
    public static class ParticipantDTO {

        @NotNull
        @ApiModelProperty(value = "참가자 엔티티 id")
        private Long id;

        @NotNull
        @ApiModelProperty(value = "참가자 프로그램 id")
        private Long programId;

        @NotNull
        @ApiModelProperty(value = "참가자 이메일")
        private String email;

        @NotNull
        @ApiModelProperty(value = "참가자 닉네임")
        private String nickname;

        @NotNull
        @ApiModelProperty(value = "참가자 역할")
        private ProgramRole role;

        @NotNull
        @ApiModelProperty(value = "프로그램 주최자 여부")
        private boolean isHost;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(description = "프로그램 참가 신청 폼")
    public static class ApplyRequest {

        @ApiModelProperty(value = "참가자 엔티티 id")
        private Long id;

        @NotNull
        @ApiModelProperty(value = "참가 신청자 닉네임")
        private String nickname;

        @NotNull
        @ApiModelProperty(value = "참가 신청 프로그램 id")
        private Long programId;

        @NotNull
        @ApiModelProperty(value = "참가 신청 메시지")
        private String message;

        @NotNull
        @ApiModelProperty(value = "참가 신청 역할")
        private ProgramRole role;
    }

}
