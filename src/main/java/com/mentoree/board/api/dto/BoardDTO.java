package com.mentoree.board.api.dto;

import com.mentoree.board.domain.Board;
import com.mentoree.member.domain.Member;
import com.mentoree.mission.domain.Mission;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class BoardDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class BoardInfo {

        private Long boardId;
        private Long missionId;
        private String missionTitle;
        private String writerNickname;
        private String content;

        @Builder
        public BoardInfo(Long boardId, Long missionId, String missionTitle, String content, String writerNickname) {
            this.boardId = boardId;
            this.missionId = missionId;
            this.missionTitle = missionTitle;
            this.writerNickname = writerNickname;
            this.content = content;
        }

        public Board toEntity(Mission mission, Member writer) {
            return Board.builder()
                    .mission(mission)
                    .writer(writer)
                    .content(content)
                    .build();
        }

    }

}
