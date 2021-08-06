package com.matching.mentoree.service.dto;

import com.matching.mentoree.domain.Member;
import com.matching.mentoree.domain.MenteeBoard;
import com.matching.mentoree.domain.MentorBoard;
import com.matching.mentoree.domain.Mission;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BoardDTO {

    private Long missionId;
    private Long memberId;
    private Long menteeBoardId;
    private String content;

    @Builder
    public BoardDTO(Long missionId, Long memberId, Long menteeBoardId, String content) {
        this.missionId = missionId;
        this.memberId = memberId;
        this.menteeBoardId = menteeBoardId;
        this.content = content;
    }

    public MenteeBoard toMenteeBoardEntity(Mission mission, Member writer) {
        return MenteeBoard.builder()
                .mission(mission)
                .writer(writer)
                .content(content)
                .build();
    }

    public MentorBoard toMentorBoardEntity(MenteeBoard menteeBoard, Member writer) {
        return MentorBoard.builder()
                .menteeBoard(menteeBoard)
                .writer(writer)
                .feedback(content)
                .build();
    }



}
