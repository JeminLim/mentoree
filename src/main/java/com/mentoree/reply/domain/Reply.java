package com.mentoree.reply.domain;

import com.mentoree.board.domain.Board;
import com.mentoree.global.domain.BaseTimeEntity;
import com.mentoree.member.domain.Member;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Reply extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "reply_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member writer;

    private String content;

    @Builder
    public Reply(Board board, Member writer, String content) {
        this.board = board;
        this.writer = writer;
        this.content = content;
    }

    //==변경로직==//
    public void updateContent(String content) { this.content = content;}

}
