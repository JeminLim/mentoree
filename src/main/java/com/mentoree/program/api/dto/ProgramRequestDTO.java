package com.mentoree.program.api.dto;

import com.mentoree.program.api.dto.ProgramDTO.ParticipatedProgramDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class ProgramRequestDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(description = "프로그램 리스트 신청 요청")
    public static class ProgramRequest {

        @NotNull
        @ApiModelProperty(value = "리스트 요청 페이지")
        private int page;

        @NotNull
        @ApiModelProperty(value = "요청자 참여 프로그램 간략 정보")
        private List<ParticipatedProgramDTO> participatedPrograms;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class RecommendProgramRequest {

        @NotNull
        @ApiModelProperty(value = "리스트 요청 페이지")
        private int page;

        @NotNull
        @ApiModelProperty(value = "요청자 참여 프로그램 간략 정보")
        private List<ParticipatedProgramDTO> participatedPrograms;

        @ApiModelProperty(value = "요청자 관심 분야")
        private List<String> interests = new ArrayList<>();

    }

}
