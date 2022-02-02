package com.mentoree.board.api.dto;

import com.mentoree.board.domain.Board;
import com.mentoree.member.domain.Member;
import com.mentoree.mission.domain.Mission;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

public class BoardDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(description = "게시글 정보")
    public static class BoardInfo {

        @ApiModelProperty(value = "게시판 id")
        private Long boardId;

        @NotNull
        @ApiModelProperty(value = "수행 미션 id")
        private Long missionId;

        @NotNull
        @ApiModelProperty(value = "수행 미션 제목")
        private String missionTitle;

        @NotNull
        @ApiModelProperty(value = "게시글 작성자")
        private String writerNickname;

        @NotNull
        @ApiModelProperty(value = "게시글 내용")
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
