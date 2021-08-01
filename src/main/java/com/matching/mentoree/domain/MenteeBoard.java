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
public class MenteeBoard extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "mentee_board_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "mission_id")
    private Mission mission;

    private String content;

    @Builder
    public MenteeBoard(Mission mission, String content) {
        Assert.notNull(mission, "mission must not be null");
        Assert.notNull(content, "content must not be null");

        this.mission = mission;
        this.content = content;

    }


}
