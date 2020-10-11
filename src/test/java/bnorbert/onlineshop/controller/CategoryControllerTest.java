package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.service.CategoryService;
import bnorbert.onlineshop.transfer.category.CategoryDto;
import bnorbert.onlineshop.transfer.category.CategoryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class CategoryControllerTest {

    @Mock
    private CategoryService mockCategoryService;

    private CategoryController categoryControllerUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        categoryControllerUnderTest = new CategoryController(mockCategoryService);
    }

    @Test
    void testSave() {

        final CategoryDto saveCategoryRequest = new CategoryDto();

        final ResponseEntity<Void> result = categoryControllerUnderTest.save(saveCategoryRequest);

        verify(mockCategoryService).save(any(CategoryDto.class));
    }

    @Test
    void testGetCategories() {

        final Page<CategoryResponse> categoryResponses = new PageImpl<>(Collections.singletonList(new CategoryResponse()));
        when(mockCategoryService.getCategories(any(Pageable.class))).thenReturn(categoryResponses);

        final ResponseEntity<Page<CategoryResponse>> result = categoryControllerUnderTest.getCategories(PageRequest.of(0, 10));

    }
}
