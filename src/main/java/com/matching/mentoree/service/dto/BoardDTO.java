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

    private String content;
    private Mission mission;
    private MenteeBoard menteeBoard;

    @Builder
    public BoardDTO(String content, Mission mission, MenteeBoard menteeBoard) {
        this.content = content;
        this.mission = mission;
        this.menteeBoard = menteeBoard;
    }

    public MenteeBoard toMenteeBoardEntity(Member writer) {
        return MenteeBoard.builder()
                .content(content)
                .mission(mission)
                .writer(writer)
                .build();
    }

    public MentorBoard toMentorBoardEntity(Member writer) {
        return MentorBoard.builder()
                .menteeBoard(menteeBoard)
                .feedback(content)
                .writer(writer)
                .build();
    }


}
