package com.mentoree.category;

import com.mentoree.category.domain.Category;
import com.mentoree.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryCommandLIneRunner implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    private static final String[] categoryList = {"IT/PROGRAMMING", "음악", "인생상담", "취업", "미술"};

    @Override
    public void run(String... args) throws Exception {
        if(categoryRepository.findAll().size() <= 0) {
            for (String categoryName : categoryList) {
                categoryRepository.save(Category.builder().categoryName(categoryName).build());
            }
        }
    }
}
