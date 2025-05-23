package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// implementation of category service
// this is where we put the logic
@Service // putting an annotation service since this is a service, like manage this as a bean.
public class CategoryServiceImpl implements CategoryService {

    // list of category model
    //private List<Category> categories = new ArrayList<>();

    // repo of category to access the data
    @Autowired
    private CategoryRepository categoryRepository;

    // logic to get all the category - GET
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // logic to create a category - POST
    @Override
    public void createCategory(Category category) {
        categoryRepository.save(category); // add to db
    }

    // logic to delete a category - DELETE
    @Override
    public String deleteCategory(Long categoryId) {

        // find an existing category in database by its id
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found")); // if category not found, return a message.

        // if there's an existing category, delete it
        categoryRepository.delete(category);
        return "Category with categoryId: " + categoryId + " deleted successfully!!";
    }

    // logic to update a category - UPDATE
    @Override
    public Category updateCategory(Category category, Long categoryId) {

        // find an existing category in database by its id
        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found")); // if category not found, return a message.

        // this sets the categoryId of the incoming category object to the categoryId passed in the method.
        category.setCategoryId(categoryId);

        // this saves the updated category object to the database using the repository’s save() method.
        savedCategory = categoryRepository.save(category);

        // finally, it returns the updated Category object.
        return savedCategory;


    }
}
