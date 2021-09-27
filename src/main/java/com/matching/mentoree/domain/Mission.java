package com.matching.mentoree.domain;

import lombok.*;
import org.apache.tomcat.jni.Local;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Mission extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "mission_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "program_id")
    private Program program;
    private String goal;
    private String title;
    private String content;
    private LocalDateTime dueDate;

    @Builder
    public Mission(Program program, String title, String content, LocalDateTime dueDate, String goal) {
        Assert.notNull(program, "program must not be null");
        Assert.notNull(title, "title must not be null");
        Assert.notNull(content, "content must not be null");
        Assert.notNull(dueDate, "dueDate must not be null");

        this.program = program;
        this.title = title;
        this.content = content;
        this.goal = goal;
        this.dueDate = dueDate;
    }

    //== 변경 로직 ==//
    public void updateTitle(String title) { this.title = title; }
    public void updateContent(String content) { this.content = content; }
    public void updateDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

}
