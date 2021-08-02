package com.matching.mentoree.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Program extends BaseTimeEntity{

    @Id @GeneratedValue
    @Column(name = "program_id")
    private Long id;

    private String programName;
    private String description;

    private int maxMember;

    private LocalDateTime endDate;

    public Program(String programName, String description, int maxMember) {
        Assert.notNull(programName, "program name must not be null");
        Assert.notNull(description, "description name must not be null");
        Assert.isTrue(maxMember > 0, "member limit must be greater than 0");

        this.programName = programName;
        this.description = description;
        this.maxMember = maxMember;
    }

}
