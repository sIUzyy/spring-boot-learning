package com.ecommerce.project.security.services;

import com.ecommerce.project.model.User;
import com.ecommerce.project.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    /*
    * This method is part of the UserDetailsService interface. Spring calls it automatically when a user tries to log in.
    * This ensures that the entire method runs in a transaction — meaning:
    * If anything fails, changes will roll back.
    * Useful for working safely with the database.
    */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // fetch the user from database
        User user = userRepository.findByusername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username " + username));

        /*
        * Converts your User entity into a UserDetailsImpl object.
        * Spring Security needs a UserDetails object to check the password, roles, etc.
        */
        return UserDetailsImpl.build(user);
    }
}
