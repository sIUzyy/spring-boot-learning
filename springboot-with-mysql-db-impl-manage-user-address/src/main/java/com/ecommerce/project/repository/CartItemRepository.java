package com.ecommerce.project.repository;

import com.ecommerce.project.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // custom method to find cart itm by product id and cart id

    /*
    * @Query(...) You're writing a custom JPQL query to fetch a specific CartItem.
    * SELECT ci FROM CartItem ci You're selecting the entity CartItem (aliased as ci).
    * WHERE ci.cart.id = ?1 This filters the cart item where the cart’s ID matches the first method parameter cartId.
    * AND ci.product.id = ?2 This filters the cart item where the product’s ID matches the second method parameter productId.
    */
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = ?1 AND ci.product.id = ?2")
    CartItem findCartItemByProductIdAndCartId(Long cartId, Long productId);

    /*
    * You're telling Spring Data JPA to execute the query you define, instead of generating it automatically.
    * "DELETE FROM CartItem ci ..." This means: delete records from the CartItem table.
    * ci is an alias for CartItem, making it easier to reference its properties in the query.
    *
    * WHERE ci.cart.id = ?1
    * Match records where the product in the cart item matches a specific product ID.
    * ?2 is the second method parameter, Long productId.
    */
    @Modifying // telling jpa im intending to modify the database with the help of this query
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = ?1 AND ci.product.id = ?2")
    void deleteCartItemByProductIdAndCartId(Long cartId, Long productId);
}
