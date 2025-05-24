package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// implementation of category service
// this is where we put the logic
@Service // putting an annotation service since this is a service, like manage this as a bean.
public class CategoryServiceImpl implements CategoryService {


    // repo of category to access the data
    @Autowired
    private CategoryRepository categoryRepository;

    // logic to get all the category - GET
    @Override
    public List<Category> getAllCategories() {

        // create a list of category and findAll
        List<Category> categories = categoryRepository.findAll();

        // if category is empty
        if(categories.isEmpty())
            // if yes, throw an APIException
            throw new APIException("No category created till now.");
        return categories; // if no, return all categories.



    }

    // logic to create a category - POST
    @Override
    public void createCategory(Category category) {

        // create a custom method called "findByCategoryName" and pass down the category name
        Category savedCategory = categoryRepository.findByCategoryName(category.getCategoryName());

        // check if category name already exist.
        if(savedCategory != null) {
            // if yes, throw the custom APIException
            throw new APIException("Category with the name " + category.getCategoryName() + " already exists");
        }
        categoryRepository.save(category); // add to db
    }

    // logic to delete a category - DELETE
    @Override
    public String deleteCategory(Long categoryId) {

        // find an existing category in database by its id
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId)); // if category not found, return a custom error handler.

        // if there's an existing category, delete it
        categoryRepository.delete(category);
        return "Category with categoryId: " + categoryId + " deleted successfully!!";
    }

    // logic to update a category - UPDATE
    @Override
    public Category updateCategory(Category category, Long categoryId) {

        // find an existing category in database by its id
        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId)); // if category not found, return a custom error handler.

        // this sets the categoryId of the incoming category object to the categoryId passed in the method.
        category.setCategoryId(categoryId);

        // this saves the updated category object to the database using the repository’s save() method.
        savedCategory = categoryRepository.save(category);

        // finally, it returns the updated Category object.
        return savedCategory;


    }
}
