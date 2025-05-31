package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
@ToString // Please automatically create a toString() method for this class
public class Product {

    // fields of our product
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productId;

    @NotBlank
    @Size(min = 3, message = "Product name must contain at least 3 characters")
    private String productName;
    private String image;

    @NotBlank
    @Size(min = 6, message = "Product name must contain at least 6 characters")
    private String description;
    private Integer quantity;
    private double price;
    private double discount;
    private double specialPrice;


    /*
    * relationship to category
    * Many product can belong to one category.
    */
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    /*
    * Each product belongs to one user (who is the seller)
    * @ManyToOne: Many products can belong to one user
    * @JoinColumn(name = "seller_id"): Use a column in the product table called seller_id to store the ID of the seller (user)
    * */
    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User user;

    /*
    * one product can have multiple cart itmes
    * this is not the owner of relationship
    */
    @OneToMany(mappedBy = "product",
               cascade = {CascadeType.PERSIST, CascadeType.MERGE},
               fetch = FetchType.EAGER)
    private List<CartItem> products = new ArrayList<>();





}
