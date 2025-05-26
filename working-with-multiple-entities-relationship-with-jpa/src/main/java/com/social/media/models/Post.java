package com.social.media.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // bidirection
    // This side owns the relationship (it has the foreign key column in the DB).
    // Each Post is linked to one SocialUser.
    @ManyToOne
    @JoinColumn(name = "user_id") // Add a user_id foreign key in the post table to point to a SocialUser.
    @JsonIgnore // It is used to exclude a field from JSON serialization and deserialization.
    private SocialUser socialUser;
}
