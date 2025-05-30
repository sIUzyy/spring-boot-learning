package com.ecommerce.project.service;

import com.ecommerce.project.payload.CartDTO;
import jakarta.transaction.Transactional;

import java.util.List;

public interface CartService {

    // method to add product to cart
    CartDTO addProductToCart(Long productId, Integer quantity);

    // method to get all carts
    List<CartDTO> getAllCarts();

    // method to get cart of user
    CartDTO getCart(String emailId, Long cartId);

    // method to update the quantity of product in the cart
    @Transactional // If anything fails inside the method, all changes will be rolled back.
    CartDTO updateProductQuantityInCart(Long productId, Integer quantity);

    // method to delete the product from cart
    String deleteProductFromCart(Long cartId, Long productId);

    // method to update products in carts
    void updateProductInCarts(Long cartId, Long productId);
}
