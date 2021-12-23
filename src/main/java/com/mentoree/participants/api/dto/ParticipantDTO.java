package com.mentoree.participants.api.dto;

import com.mentoree.program.domain.ProgramRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ParticipantDTO {

    private Long id;
    private Long programId;
    private String email;
    private String nickname;
    private ProgramRole role;
    private boolean isHost;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ApplyRequest {

        private Long id;
        private String nickname;
        private Long programId;
        private String message;
        private ProgramRole role;
    }

}
