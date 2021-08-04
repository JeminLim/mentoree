package com.matching.mentoree.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MentorBoard extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "mentor_board_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "mentee_board_id")
    private MenteeBoard menteeBoard;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member writer;

    private String feedback;


    @Builder
    public MentorBoard(MenteeBoard menteeBoard, String feedback, Member writer) {
        Assert.notNull(menteeBoard, "menteeboard must not be null");
        Assert.notNull(feedback, "feedback must not be null");
        Assert.notNull(writer, "writer must not be null");

        this.menteeBoard = menteeBoard;
        this.feedback = feedback;
        this.writer = writer;
    }

}
