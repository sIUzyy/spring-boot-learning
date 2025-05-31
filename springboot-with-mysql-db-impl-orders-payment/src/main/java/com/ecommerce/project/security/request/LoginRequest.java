package com.ecommerce.project.security.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// defining the format of the request (how should a request come into the backend server) (DTO)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
