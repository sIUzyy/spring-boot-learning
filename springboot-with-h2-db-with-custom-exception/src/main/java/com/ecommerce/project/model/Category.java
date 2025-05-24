package com.ecommerce.project.model;

// model is how "category" is structured in our application, like what are the fields.
// category - model

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity // lombok
@Data // lombok
@NoArgsConstructor // lombok
@AllArgsConstructor // lombok
// @Entity(name = "categories") // by this you can change the table name from category to categories
public class Category {

    @Id // it's important to initialize the id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @NotBlank // validation dependency (make this field not blank)
    @Size(min = 5, message = "Category name must contain at least 5 characters") // validation dependency (make this field min of 5 char)
    private String categoryName;


}
