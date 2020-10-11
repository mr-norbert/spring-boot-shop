package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.service.CategoryService;
import bnorbert.onlineshop.transfer.category.CategoryDto;
import bnorbert.onlineshop.transfer.category.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody CategoryDto saveCategoryRequest) {
        categoryService.save(saveCategoryRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/getCategories")
    public ResponseEntity<Page<CategoryResponse>> getCategories(Pageable pageable) {
        Page<CategoryResponse> categories = categoryService.getCategories(pageable);
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }
}
