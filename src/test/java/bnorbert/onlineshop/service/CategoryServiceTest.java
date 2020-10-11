package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.Category;
import bnorbert.onlineshop.mapper.CategoryMapper;
import bnorbert.onlineshop.repository.CategoryRepository;
import bnorbert.onlineshop.transfer.category.CategoryDto;
import bnorbert.onlineshop.transfer.category.CategoryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class CategoryServiceTest {

    @Mock
    private CategoryRepository mockCategoryRepository;
    @Mock
    private CategoryMapper mockCategoryMapper;

    private CategoryService categoryServiceUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        categoryServiceUnderTest = new CategoryService(mockCategoryRepository, mockCategoryMapper);
    }

    @Test
    void testSave() {
        final CategoryDto request = new CategoryDto();

        when(mockCategoryRepository.save(any(Category.class))).thenReturn(new Category());
        when(mockCategoryMapper.map(any(CategoryDto.class))).thenReturn(new Category());

        categoryServiceUnderTest.save(request);
    }

    @Test
    void testGetCategory() {
        when(mockCategoryRepository.findById(1L)).thenReturn(Optional.of(new Category()));

        final Category result = categoryServiceUnderTest.getCategory(1L);
    }

    @Test
    void testGetCategoryThenReturnResourceNotFound() {
        when(mockCategoryRepository.findById(1L)).thenReturn(Optional.of(new Category()));

        final Category result = categoryServiceUnderTest.getCategory(0L);
    }

    @Test
    void testGetCategories() {

        final Page<Category> categories = new PageImpl<>(Collections.singletonList(new Category()));
        when(mockCategoryRepository.findAll(any(Pageable.class))).thenReturn(categories);

        when(mockCategoryMapper.entitiesToEntityDTOs(Collections.singletonList(new Category()))).thenReturn(Collections.singletonList(new CategoryResponse()));

        final Page<CategoryResponse> result = categoryServiceUnderTest.getCategories(PageRequest.of(0, 10));
    }

}
