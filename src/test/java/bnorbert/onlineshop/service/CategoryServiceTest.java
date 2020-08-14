package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.Category;
import bnorbert.onlineshop.mapper.CategoryMapper;
import bnorbert.onlineshop.repository.CategoryRepository;
import bnorbert.onlineshop.transfer.category.CategoryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

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
}
