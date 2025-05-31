package com.ecommerce.project.repository;

import com.ecommerce.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // custom method to find the user by username
    Optional<User> findByusername(String username);

    // custom method to check if username already exist
    Boolean existsByusername(String username);

    // custom method to check if email already exist
    Boolean existsByEmail(String email);
}
