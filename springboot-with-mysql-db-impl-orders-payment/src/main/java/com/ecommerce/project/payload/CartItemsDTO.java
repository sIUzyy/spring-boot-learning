package com.ecommerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// request object
// like a model but not really a model
// it is representing cart items at the presentation layer
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemsDTO {

    private Long cartItemId;

    private CartDTO cart;
    private ProductDTO productDTO;

    private Integer quantity;
    private Double discount;
    private Double productPrice;
}
