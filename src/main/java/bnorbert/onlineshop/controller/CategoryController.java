package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.service.CategoryService;
import bnorbert.onlineshop.transfer.category.CategoryDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/categories")

@AllArgsConstructor
public class CategoryController {

    CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody CategoryDto saveCategoryRequest) {
        categoryService.save(saveCategoryRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
