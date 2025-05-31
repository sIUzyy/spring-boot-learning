package com.ecommerce.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "order_items")
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

    @Id // This annotation marks the next field (orderItemId) as the primary key of the OrderItem entity.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    /*
    * many-one relationship
    * each OrderItem is related to one Product, but a Product can appear in many OrderItems
    * */
    @ManyToOne
    /*
    * Specifies the foreign key column in the order_item table that links to the product table. It will be named product_id in the database.
    */
    @JoinColumn(name = "product_id")
    private Product product;

    /*
    * many-one relationship
    * many order items can have one order
    */
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private Integer quantity;
    private Double discount;
    private Double orderedProductPrice;
}
