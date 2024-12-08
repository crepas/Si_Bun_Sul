package com.sys.OrderSystem.Service;

import com.sys.OrderSystem.Entity.Category;
import com.sys.OrderSystem.Repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // 모든 카테고리 조회
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // 카테고리 저장
    @Transactional
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    // 카테고리 업데이트
    @Transactional
    public Category updateCategory(int categoryId, Category categoryDetails) {
        Optional<Category> existingCategoryOpt = categoryRepository.findById(categoryId);
        if (existingCategoryOpt.isPresent()) {
            Category existingCategory = existingCategoryOpt.get();
            existingCategory.setName(categoryDetails.getName());
            return categoryRepository.save(existingCategory);
        } else {
            throw new RuntimeException("카테고리를 찾을 수 없습니다."); // 구체적인 예외 처리
        }
    }

    // 카테고리 삭제
    @Transactional
    public boolean deleteCategory(int categoryId) {
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
        if (categoryOpt.isPresent()) {
            categoryRepository.delete(categoryOpt.get());
            return true;
        } else {
            throw new RuntimeException("삭제할 카테고리를 찾을 수 없습니다."); // 예외 처리
        }
    }
}
