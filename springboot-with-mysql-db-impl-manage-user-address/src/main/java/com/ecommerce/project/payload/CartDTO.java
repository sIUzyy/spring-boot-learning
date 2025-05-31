package com.ecommerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

// request object
// like a model but not really a model
// it is representing cart at the presentation layer
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {

    private Long cartId;
    private Double totalPrice = 0.0;
    private List<ProductDTO> products = new ArrayList<>();
}
