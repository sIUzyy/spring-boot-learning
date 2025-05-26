package com.social.media.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // bidirectional relationship 1-1 (aware to each other that have a rs)
    /*
    * “I am not the owner of the relationship. The other class (SocialProfile) is responsible for managing the relationship.
    * Just look at its user field for the mapping.”
    *
    * In JPA, only one side of a bidirectional relationship is the "owner" — the side that defines the @JoinColumn.
    *
    * The other side uses mappedBy to refer back to the owner's field.
    *
    * cascade = It refers to how operations applied to one entity are automatically applied to its related entities.
    * cascade type all = apply all the cascade type e.g. if i want to delete social user the social profile will be also deleted
    * */
//    @OneToOne(mappedBy = "user", cascade = {CascadeType.REMOVE, CascadeType.PERSIST, CascadeType.MERGE}) // example of multiple cascade
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private SocialProfile socialProfile;

    // One SocialUser has a list of posts.
    @OneToMany(mappedBy = "socialUser")  // The other side (Post) owns the relationship. Look at its socialUser field for the mapping.
    private List<Post> posts = new ArrayList<>();

    // One user can join many groups.
    //@ManyToMany(fetch = FetchType.EAGER)
    @ManyToMany(fetch = FetchType.LAZY) // fetch type lazy, don't load all the data
    // This is the owner side of the relationship socialUser-SocialGroup.
    @JoinTable(
            name = "user_group",  // name of the join table
            joinColumns = @JoinColumn(name = "user_id"),  // FK to SocialUser
            inverseJoinColumns = @JoinColumn(name = "group_id") // FK to SocialGroup
    )
    private Set<SocialGroup> groups = new HashSet<>(); // list of groups


    // override the hashcode to prevent error
    @Override
    public int hashCode(){
        return Objects.hash(id);
    }

    // custom setter to set social profile
    public void setSocialProfile(SocialProfile socialProfile){
        socialProfile.setUser(this);
        this.socialProfile = socialProfile;

    }

}
