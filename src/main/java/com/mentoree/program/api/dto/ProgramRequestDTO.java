package com.mentoree.program.api.dto;

import com.mentoree.program.api.dto.ProgramDTO.ParticipatedProgramDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public class ProgramRequestDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ProgramRequest {

        private int page;
        private List<ParticipatedProgramDTO> participatedPrograms;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class RecommendProgramRequest {

        private int page;
        private List<ParticipatedProgramDTO> participatedPrograms;
        private List<String> interests;

    }

}
