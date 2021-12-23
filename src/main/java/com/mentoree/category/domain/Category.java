package com.mentoree.category.domain;

import com.mentoree.global.domain.BaseTimeEntity;
import com.mysema.commons.lang.Assert;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Categories")
@EqualsAndHashCode
public class Category extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String categoryName;

    @Builder
    public Category(String categoryName) {
        Assert.notNull(categoryName, "category name must not be null");
        this.categoryName = categoryName;
    }
}
