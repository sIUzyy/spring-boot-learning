package com.ecommerce.project.controller;

import com.ecommerce.project.model.AppRole;
import com.ecommerce.project.model.Role;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repository.RoleRepository;
import com.ecommerce.project.repository.UserRepository;
import com.ecommerce.project.security.jwt.JwtUtils;
import com.ecommerce.project.security.request.LoginRequest;
import com.ecommerce.project.security.request.SignupRequest;
import com.ecommerce.project.security.response.MessageResponse;
import com.ecommerce.project.security.response.UserInfoResponse;
import com.ecommerce.project.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

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

    // user repository
    @Autowired
    UserRepository userRepository;

    // role repository
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;


    /*
     * This api controller runs when a user tries to log in.
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
        // Gets the user’s details like username and roles from custom user details.
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        // Calls your jwtUtils to create a JWT cookie using the generateJwtCookie. This cookie will be sent back to the user and used for future secure requests.
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
        // Extracts the list of user roles like ROLE_USER, ROLE_ADMIN.
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        /*
         * Creates a response object with:
         * Username
         * JWT Cookie
         * Roles
         * */
//        UserInfoResponse response = new UserInfoResponse(userDetails.getId(),
//                userDetails.getUsername(), roles);
        UserInfoResponse response = new UserInfoResponse(userDetails.getId(),
                userDetails.getUsername(), roles);
       /*
       * A Set-Cookie header (used to send a cookie to the browser)
       * A response body (typically JSON)
       */
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).body(response);

    }

    // sign up - CREATE an account
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(
            @Valid // automatically validates the fields inside SignupRequest (like username, email, etc.).
            @RequestBody SignupRequest signupRequest // pring will convert JSON from the request body into a SignupRequest object.
    ){
        // Checks if the username already exists in the database.
        if(userRepository.existsByusername(signupRequest.getUsername())){
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken"));
        }

        // Checks if the email is already used.
        if(userRepository.existsByEmail(signupRequest.getEmail())){
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already taken"));
        }


        // Creates a new User object using the signup data.
        User user = new User(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                passwordEncoder.encode(signupRequest.getPassword()) // Password is hashed/encrypted using passwordEncoder.
        );

        // strRoles: a set of role strings (e.g. ["admin", "seller"]) from the request.
        Set<String> strRoles = signupRequest.getRole();

        // roles: a new set to store the actual Role entities.
        Set<Role> roles = new HashSet<>();

        // If no roles were provided…
        if(strRoles == null){
            /*
            * Set default role as ROLE_USER.
            * If not found in DB, throws an exception.
            */
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
            roles.add(userRole);
        } else{
            // If roles were provided, loop through them.
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin": // If role is "admin", fetch and add ROLE_ADMIN.
                        Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                        roles.add(adminRole);
                        break;
                    case "seller": // If role is "seller", fetch and add ROLE_SELLER.
                        Role sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                        roles.add(sellerRole);
                        break;
                    default: // Any other value (e.g., "user", or unrecognized role), defaults to ROLE_USER.
                        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                        roles.add(userRole);

                }
            });
        }
        // Assigns the selected roles to the user.
        user.setRoles(roles);
        // Saves the new user (with roles) into the database.
        userRepository.save(user);
        // Returns a 200 OK response with success message.
        return ResponseEntity.ok(new MessageResponse("User registered successfully"));

    }

    // get the username of user - GET
    /*
    * Takes a Spring Security Authentication object (injected automatically)
    * Returns a String.
    */
    @GetMapping("/username")
    public String currentUserName(Authentication authentication){
        // Checks if the user is authenticated.
        if(authentication != null){
            return authentication.getName(); // If the user is authenticated, return their username
        } else {
            return ""; // If not authenticated, return an empty string.
        }
    }

    // get the user details - GET
    /*
    * Accepts the Authentication object.
    * Returns a ResponseEntity<?>
    */
    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(Authentication authentication){

        // Retrieves the authenticated user details from the Authentication object and casts it to custom UserDetailsImpl class.
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Extracts the list of user roles like ROLE_USER, ROLE_ADMIN.
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        /*
         * Creates a response object with:
         * Username
         * Roles
         */
        UserInfoResponse response = new UserInfoResponse(userDetails.getId(),
                userDetails.getUsername(), roles);
        return ResponseEntity.ok().body(response);
    }

    // sign out the user - POST
    /*
    * This defines a public method named signOutUser.
    * It returns a ResponseEntity<?>, meaning it can return any type of response body.
    */
    @PostMapping("/signout")
    public ResponseEntity<?> signOutUser(){
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie(); // is a method call that creates a cookie with the JWT cleared (null).
        /*
        * HttpHeaders.SET_COOKIE	A constant for the string "Set-Cookie"
        * cookie.toString()	Turns the ResponseCookie into a string like: jwt=; Path=/api; Max-Age=0
        * .body(new MessageResponse("You've been signed out!"))
        */
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(new MessageResponse("You've been signed out!"));
    }


}
