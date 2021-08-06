package com.matching.mentoree.domain;

import com.mysema.commons.lang.Assert;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Categories")
@EqualsAndHashCode
public class Category extends BaseTimeEntity{

    @Id @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String categoryName;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory")
    private List<Category> childCategory = new ArrayList<>();

    @Builder
    public Category(String categoryName, Category parentCategory, List<Category> childCategory) {
        Assert.notNull(categoryName, "category name must not be null");

        this.categoryName = categoryName;
        this.parentCategory = parentCategory;
        this.childCategory = childCategory;
    }



}
