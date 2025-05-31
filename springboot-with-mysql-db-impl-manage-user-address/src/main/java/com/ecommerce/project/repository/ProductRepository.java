package com.ecommerce.project.repository;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // custom method to find the product by category
    Page<Product> findByCategoryOrderByPriceAsc(Category category, Pageable pageDetails);

    // custom method to find the product by keyword
    Page<Product> findByProductNameLikeIgnoreCase(String keyword, Pageable pageDetails); // Spring Data JPA automatically provides case-insensitive query methods using naming conventions like: findByProductNameLikeIgnoreCase
}
