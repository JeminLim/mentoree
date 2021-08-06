package com.matching.mentoree.domain;

import lombok.*;
import org.springframework.util.Assert;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class MemberInterest extends BaseTimeEntity{

    @Id @GeneratedValue
    @Column(name = "member_interest_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public MemberInterest(Category category, Member member) {
        Assert.notNull(category, "category must not be null");
        Assert.notNull(member, "user must not be null");

        this.category = category;
        this.member = member;
    }


}
