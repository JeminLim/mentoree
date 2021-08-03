package com.matching.mentoree.domain;

import lombok.*;
import org.springframework.util.Assert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "programName", "description", "goal", "maxMember"})
public class Program extends BaseTimeEntity{

    @Id @GeneratedValue
    @Column(name = "program_id")
    private Long id;

    private String programName;
    private String description;
    private String goal;

    private int maxMember;

    private LocalDateTime endDate;

    @Builder
    public Program(String programName, String description, int maxMember, String goal) {
        Assert.notNull(programName, "program name must not be null");
        Assert.notNull(description, "description must not be null");
        Assert.notNull(goal, "goal must not be null");
        Assert.isTrue(maxMember > 0, "member limit must be greater than 0");

        this.programName = programName;
        this.description = description;
        this.maxMember = maxMember;
        this.goal = goal;
    }

}
