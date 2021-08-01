package com.matching.mentoree.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.util.Assert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mission extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "mission_id")
    private Long id;

    private String content;
    private LocalDateTime dueDate;

    @Builder
    public Mission(String content, LocalDateTime dueDate) {
        Assert.notNull(content, "content must not be null");
        Assert.notNull(dueDate, "dueDate must not be null");

        this.content = content;
        this.dueDate = dueDate;
    }

}
