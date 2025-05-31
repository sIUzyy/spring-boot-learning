package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id // Marks the next field (orderId) as the primary key of the table.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Email // validation
    @Column(nullable = false) // Ensures that the email field cannot be null in the database.
    private String email;

    /*
    * one-many relationship
    * one order can have multiple order item
    * mappedBy = "order" tells JPA that the order field in the OrderItem class owns the relationship.
    */
    @OneToMany(mappedBy = "order", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<OrderItem> orderItems = new ArrayList<>();

    // Stores the date when the order was placed (e.g., 2025-05-31).
    private LocalDate orderDate;


    // Establishes a one-to-one relationship between this Order and a Payment. (one order can have one payment)
    @OneToOne
    // Specifies that the foreign key in the Order table is named payment_id, linking it to the Payment entity.
    @JoinColumn(name = "payment_id")
    private Payment payment;


    private Double totalAmount;
    private String orderStatus;

    /*
    * many-one
    * many order can have same address (one)
    * */
    @ManyToOne
    @JoinColumn(name = "address_id") // Specifies that this order will store the foreign key as address_id in the database.
    private Address address;
}
