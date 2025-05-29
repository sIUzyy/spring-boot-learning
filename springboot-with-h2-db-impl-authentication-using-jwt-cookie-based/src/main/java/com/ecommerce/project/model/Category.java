package com.ecommerce.project.model;

// model is how "category" is structured in our application, like what are the fields.
// category - model
// entity

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity // lombok (by default the table in the db is Category if we didn't pass any name)
@Data // lombok
@NoArgsConstructor // lombok
@AllArgsConstructor // lombok
//@Entity(name = "categories") // by this you can change the table name from category to categories
public class Category {

    @Id // it's important to initialize the id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @NotBlank // validation dependency (make this field not blank)
    @Size(min = 5, message = "Category name must contain at least 5 characters") // validation dependency (make this field min of 5 char)
    private String categoryName;


    // one-to-many relationship with product (bi-direction)
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Product> products;


}
