package com.springboot.online_store.controllers;

import com.springboot.online_store.dtos.category.CategoryCreateDto;
import com.springboot.online_store.dtos.category.CategoryInfoDto;
import com.springboot.online_store.services.CategoryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryInfoDto> createCategory(@Valid @RequestBody CategoryCreateDto categoryDto){
        log.info("create category: {}", categoryDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.createCategory(categoryDto));
    }

    @GetMapping
    public ResponseEntity<List<CategoryInfoDto>> getCategories(
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @RequestParam(name = "pageNumber", required = false) Integer pageNumber
    ){
        log.info("get all categories");
        return ResponseEntity.ok(categoryService.getCategories(pageSize, pageNumber));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryInfoDto> updateCategory(@Valid @RequestBody CategoryCreateDto createDto, @PathVariable Long id){
        log.info("update category with id = {}", id);
        return ResponseEntity.ok(categoryService.updateCategory(createDto, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmptyCategory(@PathVariable("id") Long id){
        log.info("delete only empty category with id = {}", id);
        categoryService.deleteEmptyCategory(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/withProducts")
    public ResponseEntity<Void> deleteCategoryWIthProducts(@PathVariable("id") Long id){
        log.info("delete category with id = {}", id);
        categoryService.deleteCategoryWithProducts(id);
        return ResponseEntity.noContent().build();
    }
}
