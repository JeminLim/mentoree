package com.matching.mentoree.domain;

import com.matching.mentoree.exception.ClosedProgramException;
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

    private boolean approval;

    private LocalDateTime endDate;

    @Builder
    public Participant(Member member, Program program, ProgramRole role, boolean isHost, boolean approval) {
        Assert.notNull(member, "participant user must not be null");
        Assert.notNull(program, "participant program must not be null");
        Assert.notNull(role, "participant role must not be null");
        Assert.notNull(isHost, "isHost variable must not be null");

        this.member = member;
        this.program = program;
        this.role = role;
        this.isHost = isHost;
        this.approval = approval;
    }

    //== 비지니스 로직 ==//

    /**
     * 프로그램 참가 승인
     */
    public void approve() {
        if(program.isOpen()) {
            approval = true;
            program.addParticipant();
        } else {
            throw new ClosedProgramException("Program had been closed");
        }
    }



}
