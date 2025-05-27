package com.ecommerce.project.payload;

// request object
// like a model but not really a model
// it is representing category at the presentation layer

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    private Long categoryId;
    private String categoryName;


}
