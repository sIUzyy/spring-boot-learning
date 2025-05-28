package com.example.securityDemo.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// this class is like what you want to respond to the user once is authenticated
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String jwtToken;
    private String username;
    private List<String> roles;

}
