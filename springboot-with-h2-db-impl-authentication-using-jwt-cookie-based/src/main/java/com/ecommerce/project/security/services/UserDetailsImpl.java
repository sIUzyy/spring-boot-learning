package com.ecommerce.project.security.services;

import com.ecommerce.project.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// this class is custom userDetails
@Data
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {


    /*
    * It’s used when a class implements Serializable — which means the class’s objects can be saved to a file,
    * sent over a network, or stored in a database in binary format.
    */
    private static final long serialVersionUID = 1L;

    /*
    * These are the fields that representing the authenticated user
    * */
    private Long id;
    private String username;
    private String email;
    @JsonIgnore // ensure that password is not serialize in json response
    private String password;

    /*
    * I want to store a collection (like a list or set) of things that are either GrantedAuthority or subclasses of GrantedAuthority
    * Collection: A group of items (like a bag of roles or permissions).
    * ? extends GrantedAuthority: Means anything that is a GrantedAuthority or a child class of it (e.g., SimpleGrantedAuthority).
    * authorities: The name of the variable, usually meaning user roles or permissions.
    * GrantedAuthority - It's an interface used by Spring Security to represent an authority — like a role or permission.
    * */
    private Collection<? extends GrantedAuthority> authorities;

    // constructor
    public UserDetailsImpl(Long id, String username, String email, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    /*
    * This method is creating a new object of UserDetailsImpl based on your User entity —
    * it's converting your custom User object into a form Spring Security understands.
    *
    * Spring Security needs a UserDetails object to do login/authentication. But your app probably has its own User class (with roles, email, etc.).
    * So you use this method to build a bridge between them.
    * */
    public static UserDetailsImpl build(User user){

        /*
        * This part is converting your user's roles into something Spring Security understands (GrantedAuthority).
        * user.getRoles() – gets the list or set of Role objects.
        * .stream() – starts a stream (a Java way to process a collection).
        * .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
        *  Converts each Role into a SimpleGrantedAuthority.
        *  role.getRoleName() might return an enum like AppRole.ADMIN
        *  .name() gets the string "ADMIN"
        *  So this becomes new SimpleGrantedAuthority("ADMIN")
        * */
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
                .collect(Collectors.toList()); // .collect(Collectors.toList()) – turns the result into a List.

        /*
        * Now you're creating a new UserDetailsImpl object, passing:
        * This object can now be used by Spring Security to authenticate and authorize the user.
        * */
        return new UserDetailsImpl(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    /*
    * It helps Java know when two UserDetailsImpl objects should be treated as the same user, even if they’re two separate objects in memory.
    * */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Checks if both references point to the same exact object in memory.
        /*
        * If the object you're comparing is null, or not the same class → they're not equal.
        * getClass() != o.getClass() ensures you're not comparing different types (like UserDetailsImpl vs Product
        */
        if (o == null || getClass() != o.getClass()) return false;
        // If the type is valid, cast the object o to UserDetailsImpl so you can access its fields.
        UserDetailsImpl user = (UserDetailsImpl) o;
        // Finally, compare the two users by their id fields.
        return Objects.equals(id, user.id);
    }
}
