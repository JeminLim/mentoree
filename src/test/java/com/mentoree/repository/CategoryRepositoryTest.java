package com.mentoree.repository;

import com.mentoree.category.domain.Category;
import com.mentoree.category.repository.CategoryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    private Category testCategory;
    @BeforeEach
    void setUp() {
        testCategory = Category.builder().categoryName("testCategory").build();
        categoryRepository.save(testCategory);
    }

    @Test
    @DisplayName("이름으로 카테고리 찾기")
    void findCategoryByName() {
        Category testCategory = categoryRepository.findByCategoryName("testCategory").orElseThrow(NoSuchElementException::new);

        assertThat(testCategory.getId()).isEqualTo(testCategory.getId());
        assertThat(testCategory.getCategoryName()).isEqualTo(testCategory.getCategoryName());
    }


}
