package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.Category;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.mapper.CategoryMapper;
import bnorbert.onlineshop.repository.CategoryRepository;
import bnorbert.onlineshop.transfer.category.CategoryDto;
import bnorbert.onlineshop.transfer.category.CategoryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    public void save(CategoryDto request) {
        categoryRepository.save(categoryMapper.map(request));
    }

    public Category getCategory(long id){
        log.info("Retrieving category {}", id);
        return categoryRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Category: " + id + "not found"));
    }

    @Transactional
    public Page<CategoryResponse> getCategories(Pageable pageable){
        log.info("Retrieving categories");
        Page<Category> categories = categoryRepository.findAll(pageable);
        List<CategoryResponse> categoryResponses = categoryMapper.entitiesToEntityDTOs(categories.getContent());
        return new PageImpl<>(categoryResponses, pageable, categories.getTotalElements());
    }

}
