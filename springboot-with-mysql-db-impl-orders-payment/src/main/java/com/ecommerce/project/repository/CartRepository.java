package com.ecommerce.project.repository;

import com.ecommerce.project.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {

    /*
    * this is nested object so jpa doesn't automatically generate a query
    * custom method to find the cart by email
    * Query is a Spring Data JPA annotation used to define a custom JPQL (Java Persistence Query Language) query manually,
    * instead of relying on derived query methods like findBy....
    * */

    /*
    * SELECT c
    * This means: “select the whole Cart object using the alias c.”
    *
    * FROM Cart c
    * This says: “from the Cart entity (which maps to the cart table in your database),
    * refer to it using the alias c.”
    */

    /*
    * c.user.email = ?1:
    * This is the condition of the query. It means "where the email of the user associated with the cart is equal to the first parameter
    * (?1) passed into the method."
    * ?1 refers to the first parameter in the method (email in this case).
    */
    @Query("SELECT c FROM Cart c WHERE c.user.email = ?1")
    Cart findCartByEmail(String email);

    /*
    * SELECT c FROM Cart c:
    * This means: select the cart (c) from the Cart table (entity).
    * c is an alias used to refer to the Cart object.
    */
    // WHERE c.user.email = ?1 Checks if the email of the user who owns the cart matches the first parameter (?1).
    // AND c.id = ?2: Also checks if the cart's ID matches the second parameter (?2).
    @Query("SELECT c FROM Cart c WHERE c.user.email = ?1 AND c.id = ?2")
    Cart findCartByEmailAndCartId(String emailId, Long cartId);


    // custom method to find carts by product id
    /*
    * Finds all carts (Cart c)
    * That contain at least one cart item (cartItems ci)
    * Where the product inside that cart item has an ID matching productId
    */

    /*
    * JOIN FETCH is used to eagerly load the associations (i.e., it avoids lazy loading by loading related entities in the same query).
    * c.cartItems ci: joins Cart with its list of CartItems
    * ci.product p: joins each CartItem with its associated Product
    * WHERE p.id = ?1: filters for only those where the Product ID matches the given productId
    */
    @Query("SELECT c FROM Cart c JOIN FETCH c.cartItems ci JOIN FETCH ci.product p WHERE p.id = ?1")
    List<Cart> findCartsByProductId(Long productId);
}
