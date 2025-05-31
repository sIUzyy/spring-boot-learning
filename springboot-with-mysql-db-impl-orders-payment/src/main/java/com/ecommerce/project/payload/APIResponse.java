package com.ecommerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


// this class is for standard use for our api response in our global exception handler

@Data
@NoArgsConstructor
@AllArgsConstructor
public class APIResponse {

    public String message;
    private boolean status;

}
