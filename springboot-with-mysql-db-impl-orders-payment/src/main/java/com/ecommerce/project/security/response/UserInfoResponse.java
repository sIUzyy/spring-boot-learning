package com.ecommerce.project.security.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// this class is like what you want to respond to the user once is authenticated (DTO)
@Data
@NoArgsConstructor
//@AllArgsConstructor
public class UserInfoResponse {

    private Long id;
   // private String jwtToken;
    private String username;
    private List<String> roles;

//    public UserInfoResponse(Long id, String username, List<String> roles, String jwtToken) {
//        this.id = id;
//        this.username = username;
//        this.roles = roles;
//        this.jwtToken = jwtToken;
//    }

    public UserInfoResponse(Long id, String username, List<String> roles) {
        this.id = id;
        this.username = username;
        this.roles = roles;
    }
}
