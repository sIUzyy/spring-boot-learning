package com.example.securityDemo.controller;

import com.example.securityDemo.jwt.JwtUtils;
import com.example.securityDemo.jwt.LoginRequest;
import com.example.securityDemo.jwt.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class GreetingsController {

    /*
    * Think of AuthenticationManager as the security guard of your app. Its job is to check:
    * “Are you really who you say you are?”
    * It helps Spring Security verify a user's username and password (or other login methods).
    * A built-in Spring Security tool that handles login authentication
    * */
    @Autowired
    private AuthenticationManager authenticationManager;

    // utils we created
    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello World!";
    }

    @PreAuthorize("hasRole('USER')") // telling spring security, hey i want only users with a specific role to access this endpoint
    @GetMapping("/user")
    public String userEndpoint() {
        return "Hello User!";
    }

    @PreAuthorize("hasRole('ADMIN')") // telling spring security, hey i want only admin with a specific role to access this endpoint
    @GetMapping("/admin")
    public String adminEndpoint() {
        return "Hello Admin!";
    }

    /*
    * This method runs when a user tries to log in.
    * It expects a LoginRequest that contains a username and password.
    * ResponseEntity<?> means it returns an HTTP response — it can return different types based on success or failure.
    */
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest){
        Authentication authentication;

        try{
            /*
            * It creates a username-password token.
            * Then it asks Spring’s AuthenticationManager to verify the credentials.
            * */
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

        }catch(AuthenticationException e){
            /*
            * Builds a response that says: "Bad credentials" with a 401 status code.
            * This means the user is not authorized.
            */
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);

            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);


        }
        // if login is successful, “Hey, this user is now authenticated!”
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Gets the user’s details like username and roles.
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // Calls your jwtUtils to create a JWT token using the user's username. This token will be sent back to the user and used for future secure requests.
        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);
        // Extracts the list of user roles like ROLE_USER, ROLE_ADMIN.
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        /*
        * Creates a response object with:
        * Username
        * JWT Token
        * Roles
        * */
        LoginResponse response = new LoginResponse(userDetails.getUsername(), jwtToken, roles);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

}
