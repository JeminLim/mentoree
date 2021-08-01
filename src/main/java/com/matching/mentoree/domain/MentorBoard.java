package com.matching.mentoree.domain;

import lombok.AccessLevel;
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

    private String feedback;

    public MentorBoard(MenteeBoard menteeBoard, String feedback) {
        Assert.notNull(menteeBoard, "menteeboard must not be null");
        Assert.notNull(feedback, "feedback must not be null");

        this.menteeBoard = menteeBoard;
        this.feedback = feedback;
    }

}
