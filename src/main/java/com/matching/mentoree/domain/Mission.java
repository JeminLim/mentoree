package com.matching.mentoree.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mission extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "mission_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "program_id")
    private Program program;

    private String title;
    private String content;
    private LocalDateTime dueDate;

    @Builder
    public Mission(Program program, String title, String content, LocalDateTime dueDate) {
        Assert.notNull(program, "program must not be null");
        Assert.notNull(title, "title must not be null");
        Assert.notNull(content, "content must not be null");
        Assert.notNull(dueDate, "dueDate must not be null");

        this.program = program;
        this.title = title;
        this.content = content;
        this.dueDate = dueDate;
    }

}
