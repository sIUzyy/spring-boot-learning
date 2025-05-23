package com.ecommerce.project.repository;

import com.ecommerce.project.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

// interface to put the necessary method - data access
// extends the JpaRepository to get some useful method
// in parameter it's the type and the primary key
public interface CategoryRepository extends JpaRepository<Category, Long> {

}
