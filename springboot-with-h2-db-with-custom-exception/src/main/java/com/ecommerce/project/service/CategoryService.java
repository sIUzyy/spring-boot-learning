package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;

import java.util.List;

// interface to put the necessary method - logic


public interface CategoryService {

    // method to get all the categories
    List<Category> getAllCategories();

    // method to create a category - no need to return anything
    void createCategory(Category category);

    // method to delete a category - return a string msg
    String deleteCategory(Long categoryId);

    // method to update a category - return the updated object(category)
    Category updateCategory(Category category, Long categoryId);


}
