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
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "program_id")
    private Program program;

    @Enumerated(EnumType.STRING)
    private ProgramRole role;

    private boolean isHost;
    private LocalDateTime endDate;

    @Builder
    public Participant(User user, Program program, ProgramRole role, boolean isHost) {
        Assert.notNull(user, "participant user must not be null");
        Assert.notNull(program, "participant program must not be null");
        Assert.notNull(role, "participant role must not be null");
        Assert.notNull(isHost, "isHost variable must not be null");

        this.user = user;
        this.program = program;
        this.role = role;
        this.isHost = isHost;
    }
}
