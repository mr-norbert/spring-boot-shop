package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.service.CategoryService;
import bnorbert.onlineshop.transfer.category.CategoryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
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
}
