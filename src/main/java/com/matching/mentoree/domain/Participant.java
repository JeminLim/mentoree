package com.matching.mentoree.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Participant extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "participant_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "program_id")
    private Program program;

    @Enumerated(EnumType.STRING)
    private ProgramRole role;

    private boolean isHost;
    private LocalDateTime endDate;

    @Builder
    public Participant(Member member, Program program, ProgramRole role, boolean isHost) {
        Assert.notNull(member, "participant user must not be null");
        Assert.notNull(program, "participant program must not be null");
        Assert.notNull(role, "participant role must not be null");
        Assert.notNull(isHost, "isHost variable must not be null");

        this.member = member;
        this.program = program;
        this.role = role;
        this.isHost = isHost;
    }
}
