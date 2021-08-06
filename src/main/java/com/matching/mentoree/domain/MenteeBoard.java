package com.matching.mentoree.domain;

import lombok.*;
import org.springframework.util.Assert;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class MenteeBoard extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "mentee_board_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "mission_id")
    private Mission mission;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member writer;

    private String content;

    @Builder
    public MenteeBoard(Mission mission, String content, Member writer) {
        Assert.notNull(mission, "mission must not be null");
        Assert.notNull(content, "content must not be null");
        Assert.notNull(writer, "writer must not be null");

        this.mission = mission;
        this.content = content;
        this.writer = writer;
    }

    //== 변경 로직 ==//
    public void updateContent(String content) { this.content = content; }


}
