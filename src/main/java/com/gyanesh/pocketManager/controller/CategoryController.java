package com.gyanesh.pocketManager.controller;

import com.gyanesh.pocketManager.dto.CategoryDto;
import com.gyanesh.pocketManager.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDto> saveCategory(@RequestBody CategoryDto categoryDto){

        log.info("started saving");
        CategoryDto savedCategory = categoryService.saveCategory(categoryDto);
        log.info("saved ");
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCurrentUserCategories(){
        List<CategoryDto> currentUserCategories = categoryService.getCategoriesForCurrentUser();
        return ResponseEntity.ok(currentUserCategories);
    }

    @GetMapping("/{type}")
    public ResponseEntity<List<CategoryDto>> getCurrentUserCategoriesByType(@PathVariable String type){

        List<CategoryDto> currentUserCategories = categoryService.getCategoriesForCurrentUserByType(type);
        return ResponseEntity.ok(currentUserCategories);

    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long categoryId, @RequestBody CategoryDto categoryDto){

        CategoryDto updatedCategory = categoryService.updateCategory(categoryId, categoryDto);

        return ResponseEntity.ok(updatedCategory);

    }

}
