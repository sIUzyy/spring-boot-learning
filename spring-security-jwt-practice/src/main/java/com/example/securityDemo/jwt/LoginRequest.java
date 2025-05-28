package com.example.securityDemo.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// defining the format of the request (how should a request come into the backend server)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    private String username;
    private String password;
}
