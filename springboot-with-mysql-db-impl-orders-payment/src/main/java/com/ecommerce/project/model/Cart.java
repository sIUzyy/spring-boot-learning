package com.ecommerce.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "carts")
@NoArgsConstructor
@AllArgsConstructor
public class Cart {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    /*
    * one-one relationship with user
    * each user only have one cart
    * this is the owner of relationship
    */
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    /*
    * one-many relationship with cart items
    * one cart can have multiple item(cart items)
    * this is not the owner of relationship
    */
    @OneToMany(mappedBy = "cart",
               cascade = {CascadeType.PERSIST, // When you save a Cart, its CartItems will also be saved.
               CascadeType.MERGE,}, //  When you update a Cart, its CartItems will also be updated.
               orphanRemoval = true) // This means that if a CartItem is removed from the cartItems list, it will also be deleted from the database.
    private List<CartItem> cartItems = new ArrayList<>(); // The collection type used to hold many CartItem objects.

    private Double totalPrice = 0.0;

}
