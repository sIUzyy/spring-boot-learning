package com.social.media.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One group can have many users.
    @ManyToMany(mappedBy = "groups") // It uses mappedBy = "groups" to point back to the owner field in SocialUser class.
    @JsonIgnore // It is used to exclude a field from JSON serialization and deserialization.
    private Set<SocialUser> socialUsers = new HashSet<>(); // list of user

    // override the hashcode to prevent error
    @Override
    public int hashCode(){
        return Objects.hash(id);
    }

}
