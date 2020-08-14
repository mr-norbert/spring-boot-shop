package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.Category;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.mapper.CategoryMapper;
import bnorbert.onlineshop.repository.CategoryRepository;
import bnorbert.onlineshop.transfer.category.CategoryDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class CategoryService {

    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    public void save(CategoryDto request) {
        categoryRepository.save(categoryMapper.map(request));
    }

    public Category getCategory(long id){
        log.info("Retrieving category {}", id);
        return categoryRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Category: " + id + "not found"));
    }

}
