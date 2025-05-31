package com.ecommerce.project.controller;


import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


// category - controller (api)
// in the controller, we define the routing (endpoints), but keep business logic in the service layer
@RestController
@RequestMapping("/api") // base path - always have an "api" at the start of endpoint
public class CategoryController {

    @Autowired // field injection
    private CategoryService categoryService;



    // method/endpoint to get all the categories
    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(
            // requestparam in our url with the name of page number/size, with the default value of in app constants, required to false also its integer parameter
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORIES_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder",  defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder
    ) {
        // get the method from categoryService to get the list of categories
        CategoryResponse categoryResponse = categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
    }

    // create a category and add it to our category list
    // This method returns a ResponseEntity — a special type of object that lets you include both data and HTTP status code in your response.
    //In this case, it returns a CategoryDTO (the newly created category).
    @PostMapping("/public/categories")
    public ResponseEntity<CategoryDTO> createCategory(
            @Valid // (validation)
            @RequestBody CategoryDTO categoryDTO // fields i want in my request body
    ) {
        // create an object and get the method from categoryService then pass the categoryDTO
        CategoryDTO savedCategoryDTO = categoryService.createCategory(categoryDTO);

        // This sends back the saved category with an HTTP 201 Created status to let the user know:
        // “Your new category was successfully created!”
        return new ResponseEntity<>(savedCategoryDTO, HttpStatus.CREATED);
    }

    // delete a category by id
    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId) {
            // if success, get the method from categoryService and pass the categoryId
            CategoryDTO deletedCategory = categoryService.deleteCategory(categoryId);

            // return the deletedCategory object with status code
            return new ResponseEntity<>(deletedCategory, HttpStatus.OK);
    }

    // update a category by id
    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(
            @Valid // (validation)
            @RequestBody CategoryDTO categoryDTO, // fields i want in my request body
            @PathVariable Long categoryId // this is in the URL path
    ) {
            // create an object and get the method from categoryService and pass the categoryDTO and the categoryId
            CategoryDTO savedCategoryDTO = categoryService.updateCategory(categoryDTO, categoryId);


            // This sends a response back to the client with:
            // The updated category
            // An HTTP 200 OK status (means success)
            return new ResponseEntity<>(savedCategoryDTO, HttpStatus.OK);
    }

}
