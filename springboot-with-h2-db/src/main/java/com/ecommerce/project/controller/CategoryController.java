package com.ecommerce.project.controller;


import com.ecommerce.project.model.Category;
import com.ecommerce.project.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

// category - controller (api)
// in the controller, we define the routing (endpoints), but keep business logic in the service layer
@RestController
@RequestMapping("/api") // base path - always have an "api" at the start of endpoint
public class CategoryController {

    @Autowired // field injection
    private CategoryService categoryService;

    // method/endpoint to get all the categories
    @GetMapping("/public/categories")
    public ResponseEntity<List<Category>> getCategories() {
        // get the method from categoryService to get the list of categories
        List<Category> categories = categoryService.getAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    // create a category and add it to our category list
    @PostMapping("/public/categories")
    public ResponseEntity<String> createCategory(@RequestBody Category category) {
        // get the method from categoryService then pass the category
        categoryService.createCategory(category);
        return new ResponseEntity<>("Category created successfully", HttpStatus.CREATED);
    }

    // delete a category by id
    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {

        // try-catch
        try{
            // if success, get the method from categoryService and pass the categoryId
            String status = categoryService.deleteCategory(categoryId);
            return new ResponseEntity<>(status, HttpStatus.OK);
        } catch(ResponseStatusException e){
            // if there's an error
            return new ResponseEntity<>(e.getReason(), e.getStatusCode());

        }

    }

    // update a category by id
    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<String> updateCategory(@RequestBody Category category, @PathVariable Long categoryId) {
        try {
            // get the method from categoryService and pass the category and the categoryId
            Category savedCategory = categoryService.updateCategory(category, categoryId);
            return new ResponseEntity<>("Category with category id: " + categoryId, HttpStatus.OK);
        } catch(ResponseStatusException e){
            return new ResponseEntity<>(e.getReason(), e.getStatusCode());
        }
    }

}
