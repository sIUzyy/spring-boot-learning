package com.ecommerce.project.service;

import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {

    // creating a product - POST
    ProductDTO addProduct(Long categoryId, ProductDTO productDTO);

    // get all product - GET
    ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    // get all product by category - GET
    ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    // get all product by keyword - GET
    ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    // update a product - PUT
    ProductDTO updateProduct(Long productId, ProductDTO productDTO);

    // delete a product - DELETE
    ProductDTO deleteProduct(Long productId);

    // update a product image - PUT
    ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;
}
