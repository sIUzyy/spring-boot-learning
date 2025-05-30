package com.ecommerce.project.util;

import com.ecommerce.project.model.User;
import com.ecommerce.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

    @Autowired
    UserRepository userRepository;

    // This method returns the email of the currently logged-in user.
    public String loggedInEmail(){
        /*
        * Gets the current Spring Security authentication object.
        * This contains the username (usually the logged-in user's email or username).
        */
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Uses the username to fetch the full User object from the database.
        User user = userRepository.findByusername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + authentication.getName()));

        // Returns the user's email.
        return user.getEmail();
    }

    /*
    * Returns the user ID of the currently logged-in user.
    * The logic is the same as loggedInEmail() but instead of returning the email, it returns:
    */
    public Long loggedInUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByusername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + authentication.getName()));

        return user.getUserId();
    }

    /*
    * Returns the full User object of the logged-in user.
    * Again, same pattern: get the authentication, look up the user, and return the full object:
    */
    public User loggedInUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByusername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + authentication.getName()));
        return user;

    }


}
