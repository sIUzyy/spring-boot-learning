package com.ecommerce.project.controller;

import com.ecommerce.project.model.Cart;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.service.CartService;
import com.ecommerce.project.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private CartRepository cartRepository;


    // add product to cart - POST
    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(
            @PathVariable Long productId,
            @PathVariable Integer quantity
    ){
        CartDTO cartDTO = cartService.addProductToCart(productId, quantity);
        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.CREATED);
    }

    // It's used to retrieve all shopping carts in the system. - GET
    @GetMapping("/carts")
    // It returns a ResponseEntity containing a list of CartDTO objects
    public ResponseEntity<List<CartDTO>> getCarts(){
        // This service method probably fetches all Cart entities from the database, converts them to CartDTO format (simplified view), and returns them as a list.
        List<CartDTO> cartDTOS = cartService.getAllCarts();
        return new ResponseEntity<List<CartDTO>>(cartDTOS, HttpStatus.OK);
    }

    // It's a GET request to retrieve the cart for the currently logged-in user.
    @GetMapping("/carts/users/cart")
    // This method returns a ResponseEntity that contains a CartDTO (the cart details).
    public ResponseEntity<CartDTO> getCartById(){
        // Uses the AuthUtil class to get the email of the logged-in user
        String emailId = authUtil.loggedInEmail();

        // Finds the Cart associated with the logged-in user's email.
        Cart cart = cartRepository.findCartByEmail(emailId);

        // Gets the cart's ID from the Cart object.
        Long cartId = cart.getCartId();

        /*
        * Calls the getCart() method in the service layer.
        * It returns a CartDTO for the given user and cart ID.
        */
        CartDTO cartDTO = cartService.getCart(emailId, cartId);

        // Returns the cart data as a CartDTO with status 200 OK to indicate the request was successful.
        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.OK);

    }

    // update the quantity of item in the cart - PUT
    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateCartProduct(@PathVariable Long productId,
                                                     @PathVariable String operation
    ){
        /*
        * This calls a method in the cartService class.
        * If operation is "delete" (case-insensitive), it passes -1. Otherwise, it passes 1.
        * So you're either decreasing or increasing the quantity of the product with that productId.
        * */
        CartDTO cartDTO = cartService.updateProductQuantityInCart(productId, operation.equalsIgnoreCase("delete") ? -1 : 1);

        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.OK);
    }

    // delete the product from the cart - DELETE
    @DeleteMapping("/carts/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Long cartId, @PathVariable Long productId){

        // Calls your service layer to perform the deletion logic.
        String status = cartService.deleteProductFromCart(cartId, productId);
        return new ResponseEntity<String>(status, HttpStatus.OK);

    }
}
