package com.ecommerce.project.repository;

import com.ecommerce.project.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

// interface to put the necessary method - data access
// extends the JpaRepository to get some useful method
// in parameter it's the type and the primary key
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // custom method to find the category by category name
    Category findByCategoryName(String categoryName);
}
