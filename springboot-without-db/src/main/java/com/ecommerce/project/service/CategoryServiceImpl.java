package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;
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
    private List<Category> categories = new ArrayList<>();

    // variable for id
    private Long nextId = 1L;

    // logic to get all the category - GET
    @Override
    public List<Category> getAllCategories() {
        return categories;
    }

    // logic to create a category - POST
    @Override
    public void createCategory(Category category) {
        category.setCategoryId(nextId++); // increment the id by 1
        categories.add(category); // add to list
    }

    // logic to delete a category - DELETE
    @Override
    public String deleteCategory(Long categoryId) {
        Category category = categories.stream() // categories is a list, then stream loop through list, but it gives you a more powerful and cleaner way to handle data.
                .filter(c -> c.getCategoryId().equals(categoryId)) // filter to check the id is equals to id i want to delete
                .findFirst() // find the first match,
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found")); // if it doesn't exist then throw ResponseStatusException and show a msg.



        categories.remove(category); // then remove it from the list
        return "Category with categoryId: " + categoryId + " deleted successfully!!";
    }

    // logic to update a category - UPDATE
    @Override
    public Category updateCategory(Category category, Long categoryId) {
        // we used optional bcz maybe the category was found, or maybe it wasn't
        Optional<Category> optionalCategory = categories.stream() // categories is a list, then stream loop through list, but it gives you a more powerful and cleaner way to handle data.
                .filter(c -> c.getCategoryId().equals(categoryId)) // filter to check the id is equals to id i want to update
                .findFirst(); // find the first match,

        // check if that category with specific id is present
        if(optionalCategory.isPresent()) {
            // if true, then get that category
            Category existingCategory = optionalCategory.get();

            // set the category name (update the name)
            existingCategory.setCategoryName(category.getCategoryName());

            // return the updated category
            return existingCategory;
        } else{
            // if false, return not found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
        }


    }
}
