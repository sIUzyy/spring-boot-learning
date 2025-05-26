package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

// implementation of category service
// this is where we put the logic
@Service // putting an annotation service since this is a service, like manage this as a bean.
public class CategoryServiceImpl implements CategoryService {


    // repo of category to access the data
    @Autowired
    private CategoryRepository categoryRepository;

    // model mapper
    @Autowired
    private ModelMapper modelMapper;

    // logic to get all the category - GET
    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        /*
        * Sort is a class provided by Spring Data JPA that is used to specify how to sort data when retrieving it from the database.
        * This checks if the variable sortOrder (e.g., a user input) is "asc" (for ascending), ignoring upper/lowercase.
        * What's Sort.by(sortBy): Sort.by("name") → means "sort by the name field".
        * .ascending() or .descending() → tells it the direction of sorting.
        * */
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();


        // Pageable is an interface in Spring that tells the system how to paginate (split into pages).
        /*
        * PageRequest.of(pageNumber, pageSize):
        * Creates a page request for a specific page of data.
        * pageNumber: which page you want (starts from 0).
        * pageSize: how many items you want on one page.
        * sortByAndOrder: by ascending or descending
        */
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        /*
        * categoryRepository.findAll(pageDetails):
        * Fetches the paginated list of categories from your database.
        * Page<Category> holds: A list of Category items. Extra info like total pages, current page, total items, etc.
        */
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);

        // fetching a list of category and findAll
        // This line fetches all the categories from the database using the categoryRepository.
        List<Category> categories = categoryPage.getContent();

        // if category is empty
        if(categories.isEmpty())
            // if yes, throw an APIException
            throw new APIException("No category created till now.");

        // object mapping
        // It loops through each Category entity and converts it into a CategoryDTO using ModelMapper.
        // Why? DTOs are safer to send to the frontend — they only contain the data you want to expose (not things like internal IDs, passwords, etc.)
        List<CategoryDTO> categoryDTOS = categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();

        // Creates a CategoryResponse object.
        // Then sets its content to the list of converted CategoryDTOs.
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);

        // meta data for our pagination
        categoryResponse.setPageNumber(categoryPage.getNumber()); // page number
        categoryResponse.setPageSize(categoryPage.getSize()); // page size
        categoryResponse.setTotalElements(categoryPage.getTotalElements()); // total elements
        categoryResponse.setTotalPages(categoryPage.getTotalPages()); // total pages
        categoryResponse.setLastPage(categoryPage.isLast()); // last page boolean


        // Finally, it returns the response containing the list of categories.
        return categoryResponse;
    }

    // logic to create a category - POST
    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {

        /*
        * This converts the CategoryDTO (just a data object) into a Category entity (a format that can be saved in the database).
        * We use ModelMapper to avoid writing manual conversion code.
        */
        Category category = modelMapper.map(categoryDTO, Category.class);

        // create a custom method called "findByCategoryName" and pass down the category name
        Category categoryFromDB = categoryRepository.findByCategoryName(category.getCategoryName());

        // check if category name already exist.
        if(categoryFromDB != null) {
            // if yes, throw the custom APIException
            throw new APIException("Category with the name " + category.getCategoryName() + " already exists");
        }

        // If it doesn’t exist yet, we go ahead and save it to the database.
       Category savedCategory =  categoryRepository.save(category);

        // After saving, we convert the saved entity back to a DTO.
       return modelMapper.map(savedCategory, CategoryDTO.class);

    }

    // logic to delete a category - DELETE
    @Override
    public CategoryDTO deleteCategory(Long categoryId) {

        // find an existing category in database by its id
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId)); // if category not found, return a custom error handler.

        // if there's an existing category, delete it
        categoryRepository.delete(category);

        // After saving, we convert the category entity back to a DTO.
        return modelMapper.map(category, CategoryDTO.class);
    }

    // logic to update a category - UPDATE
    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {

        // find an existing category in database by its id
        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId)); // if category not found, return a custom error handler.

        /*
         * This converts the CategoryDTO (just a data object) into a Category entity (a format that can be saved in the database).
         * We use ModelMapper to avoid writing manual conversion code.
         */
        Category category = modelMapper.map(categoryDTO, Category.class);

        // this sets the categoryId of the incoming category object to the categoryId passed in the method.
        category.setCategoryId(categoryId);

        // this saves the updated category object to the database using the repository’s save() method.
        savedCategory = categoryRepository.save(category);

        // After saving, we convert the saved entity back to a DTO.
        return modelMapper.map(savedCategory, CategoryDTO.class);


    }
}
