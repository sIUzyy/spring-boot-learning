package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;

/*
* getAllCategories() CategoryResponse	Because you're getting a list of categories and possibly some extra info.
* createCategory(CategoryDTO category)	CategoryDTO	Because you're just creating one category, and it returns the data of that single created category.
*
*/


// interface to put the necessary method - logic

public interface CategoryService {

    // method to get all the categories (DTO implementation)
    CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    // method to create a category - no need to return anything (DTO implementation)
    CategoryDTO createCategory(CategoryDTO categoryDTO);

    // method to delete a category - return a string msg
    CategoryDTO deleteCategory(Long categoryId);

    // method to update a category - return the updated object(category)
    CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId);


}
