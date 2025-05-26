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
public class SocialProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // relationship to socialUser model 1-1 (entity)
    @OneToOne
    /*
    * The @JoinColumn annotation is used to specify how two database tables are connected —
    * typically when you're working with relationships between entities (like One-to-One, One-to-Many, etc.).
    * renaming
    */
   @JoinColumn(name = "social_user")
    @JsonIgnore // It is used to exclude a field from JSON serialization and deserialization.
    private SocialUser user;

   private String description;

   public void setSocialUser(SocialUser socialUser) {
       this.user = socialUser;
       if(user.getSocialProfile() != this) {
           user.setSocialProfile(this);
       }
   }

}
