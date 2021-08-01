package com.matching.mentoree.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProgramCategory extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "program_category_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "program_id")
    private Program program;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Builder
    public ProgramCategory(Program program, Category category) {
        Assert.notNull(program, "program must not be null");
        Assert.notNull(category, "category must not be null");

        this.program = program;
        this.category = category;
    }

}
