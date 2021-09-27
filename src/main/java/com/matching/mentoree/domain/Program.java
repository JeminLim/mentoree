package com.matching.mentoree.domain;

import lombok.*;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Program extends BaseTimeEntity{

    @Id @GeneratedValue
    @Column(name = "program_id")
    private Long id;

    //변경 가능
    private String programName;
    private String description;
    private String goal;
    private int maxMember;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    //========================//

    @OneToMany(mappedBy = "program")
    private List<Participant> participants = new ArrayList<>();

    private int curNum;
    private boolean isOpen;
    private LocalDateTime endDate;


    @Builder
    public Program(String programName, String description, int maxMember, String goal, Category category) {
        Assert.notNull(programName, "program name must not be null");
        Assert.notNull(description, "description must not be null");
        Assert.notNull(goal, "goal must not be null");
        Assert.isTrue(maxMember > 0, "member limit must be greater than 0");

        this.programName = programName;
        this.description = description;
        this.maxMember = maxMember;
        this.goal = goal;
        this.isOpen = true;
        this.curNum = 1;
        this.category = category;
    }

    //== 변경 로직 ==//
    public void updateName(String programName) { this.programName = programName; }
    public void updateDescription(String description) { this.description = description; }
    public void updateGoal(String goal) { this.goal = goal; }
    public void updateMaxMember(int maxMember) { this.maxMember = maxMember; }

    //== 비지니스 로직 ==//
    /**
     * 멤버 충원 (목표 인원 충원 시 모집 종료 )
     */
    public void addParticipant() {
        this.curNum += 1;
        isOpen = curNum < maxMember ? true : false;
    }

}
