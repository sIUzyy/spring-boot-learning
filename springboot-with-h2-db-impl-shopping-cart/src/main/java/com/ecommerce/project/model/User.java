package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
/*
* The name of the table in the database will be "users"
* uniqueConstraints = These make sure that no duplicate values are allowed for certain columns.
* Make sure username and email are unique (no duplicates allowed)
*/
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @NotBlank // validation
    @Size(max = 20) // validation
    @Column(name = "username")
    private String username;

    @NotBlank // validation
    @Size(max = 50) // validation
    @Email // validation
    @Column(name = "email")
    private String email;

    @NotBlank // validation
    @Size(max = 120) // validation
    @Column(name = "password")
    private String password;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    @Setter
    @Getter
    /*
    * many-many relationship with Role model
    * One user can have many roles, and one role can belong to many users.
    * cascade: If you save or update a user, it will also save/update the roles linked to them.
    * fetch = FetchType.EAGER: This means "whenever you load a user, also load their roles right away.
    * */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE},
    fetch = FetchType.EAGER)
    /*
    * This says: "Create a new table in the database called user_role to connect users and roles."
    * user_id: Refers to the user
    * role_id: Refers to the role
    * */
    @JoinTable(name = "user_role",
               joinColumns = @JoinColumn(name = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>(); // This means: Each user has a set of roles (like a list, but no duplicates).

    /*
    * Product is the owning side of the relationship.
    * A user (seller) can have many products
    * @OneToMany: One user → many products
    * mappedBy = "user": This matches the user field from the Product class
    * cascade = ...: If you save or update the user, it will also save or update their products
    * orphanRemoval = true: If a product is removed from the products set and not assigned to another user,
    * it will be deleted from the database automatically
    * */
    @ToString.Exclude // @ToString.Exclude: Don’t include this field in the auto-generated toString() method
    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE},
    orphanRemoval = true)
    private Set<Product> products;


    @Getter
    @Setter
    /*
    * many-many relationship with Address model
    * One user can have many address, and one address can belong to many users.
    * cascade: If you save or update a user, it will also save/update the roles linked to them.
    * */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    /*
     * This says: "Create a new table in the database called user_address to connect users and address."
     * user_id: Refers to the user
     * address_id: Refers to the address
     * */
    @JoinTable(name = "user_address",
               joinColumns = @JoinColumn(name = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "address_id")
    )
    private List<Address> addresses = new ArrayList<>(); // A list that can store multiple Address objects.

    @ToString.Exclude // This prevents infinite loops when two classes refer to each other (called circular references).
    /*
    * one user can have one cart
    */
    @OneToOne(mappedBy = "user",
              cascade = {CascadeType.PERSIST,
              CascadeType.MERGE},
              orphanRemoval = true)
    private Cart cart;


}
