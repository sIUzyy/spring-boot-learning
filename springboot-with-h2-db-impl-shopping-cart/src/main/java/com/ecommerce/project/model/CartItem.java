package com.ecommerce.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "cart_items")
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;

    /*
    * can have multiple items (cart item) in one cart
    *
    * */
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;


    /*
    * Many CartItems can be linked to one Product.
    */
    @ManyToOne
    /*
    * This annotation tells JPA which column in the database table will act as the foreign key to link to the Product table.
    */
    @JoinColumn(name = "product_id")
    private Product product;

    private  Integer quantity;
    private double discount;
    private double productPrice;

}
