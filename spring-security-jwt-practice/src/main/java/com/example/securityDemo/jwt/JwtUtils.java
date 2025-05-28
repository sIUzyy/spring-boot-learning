package com.example.securityDemo.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component // Spring, please manage this class for me so I can use it anywhere with @Autowired
public class JwtUtils {

    /*
    * “Create a tool to log messages (for debugging or monitoring).”
    * Logger is like a print tool for developers — but more powerful than System.out.println().
    * LoggerFactory.getLogger(JwtUtils.class) creates a logger tied to this class.
    * You can log:
    * Info: logger.info(...)
    * Warnings: logger.warn(...)
    * Errors: logger.error(...)
    * Debug messages: logger.debug(...)
    */
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    // for jwt expiration in milliseconds
    @Value("${spring.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    // for generating signing key
    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    // getting jwt from header
    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization"); // Get the "Authorization" header:
        logger.debug("Authorization Header: {}", bearerToken); // Log the header (for debugging):
        if(bearerToken != null && bearerToken.startsWith("Bearer ")) { // Check if it starts with "Bearer ":
            return bearerToken.substring(7); // This removes the "Bearer " prefix and returns just the token.
        }
        return null; // If the header is missing or invalid:
    }

    // generating token from username
    /*
    * This method accepts a UserDetails object (usually from Spring Security) and returns a JWT token as a String.
    */
    public String generateTokenFromUsername(UserDetails userDetails){
        String username = userDetails.getUsername(); // This gets the username from the user who is logging in.
        return Jwts.builder() // using the Jwts.builder() to start creating a JWT token using the jjwt library.
                .subject(username) // The subject is usually used to identify who the token is for.
                .issuedAt(new Date()) // Sets the current time as the token's creation time.
                .expiration(new Date((new Date().getTime() + jwtExpirationMs))) // Sets the expiration time of the token — how long it’s valid.
                .signWith(key()) // This signs the token using a secret key, to make sure it hasn’t been tampered with. key() returns a secret key or private key used for encryption.
                .compact(); // This finishes building the token and returns it as a compact string — a standard JWT format
    }

    // getting username from jwt token
    // When a user sends a request with a JWT, this code helps your backend find out who the user is.
    public String getUsernameFromJWTToken(String token) {
        return Jwts.parser() // This creates a JWT parser – basically a tool that can read and verify JWT tokens.
                .verifyWith((SecretKey) key()) // The key() method is your private or secret key that was used when you generated the token.
                .build() // This finalizes the setup of the parser so it's ready to parse the token.
                .parseSignedClaims(token) // This reads the token and verifies it. If it's valid, it extracts the claims (the data inside the token).
                .getPayload() // This gets the actual data from the token — like the username, issue date, expiration, etc.
                .getSubject(); // This extracts the subject field from the token — which is the username you put there when you created the token.

    }

    // generate signing key
    /*
    * It creates a secret key that will be used to:
    * Sign JWT tokens when creating them
    * Verify JWT tokens when reading them
    */
    public Key key(){
        return Keys.hmacShaKeyFor( // This creates a Key object using the HMAC-SHA algorithm (which is very secure). This key will be used to sign or verify the JWT.
                Decoders.BASE64.decode(jwtSecret) // This decodes the Base64 secret string back into raw bytes, because the key has to be in bytes format for signing.
        );
    }

    // validate jwt token
    // This method checks if a JWT token (called authToken) is valid and not expired or tampered with.
    public boolean validateJwtToke(String authToken){
        try{
            System.out.println("Validate");
            Jwts.parser() // Starts a JWT parser
                    .verifyWith((SecretKey) key()) // Uses your secret key to verify that the token hasn’t been changed
                    .build() //  Builds the parser
                    .parseSignedClaims(authToken); // Actually tries to read and verify the token

            return true; // If everything works, the token is valid
        }catch (MalformedJwtException exception){ // Means: The token format is wrong or broken.
            logger.error("Invalid JWT token: {}", exception.getMessage());
        } catch (ExpiredJwtException exception){ // Means: The token is too old and is no longer valid.
            logger.error("JWT token is expired: {}", exception.getMessage());
        } catch (UnsupportedJwtException exception){ // Means: The token has something in it that your system doesn’t recognize.
            logger.error("JWT token is unsupported: {}", exception.getMessage());
        } catch (IllegalArgumentException exception){ // Means: The token is empty or null.
            logger.error("JWT claims string is empty: {}", exception.getMessage());
        }
        return false; // return false if token is invalid
    }
}
