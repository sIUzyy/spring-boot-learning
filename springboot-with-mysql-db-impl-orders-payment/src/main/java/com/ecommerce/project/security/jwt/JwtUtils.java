package com.ecommerce.project.security.jwt;

import com.ecommerce.project.security.services.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.WebConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

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

    // for jwt cookie
    @Value("${spring.ecom.app.jwtCookieName}")
    private String jwtCookie;



    // getting jwt from cookies
    /*
    * Declares a public method named getJwtFromCookies.
    * It takes an HttpServletRequest (representing the incoming HTTP request) as input and returns a String (the JWT).
    */
    public String getJwtFromCookies(HttpServletRequest request) {
        /*
        * Uses Spring's WebUtils.getCookie() to search for a cookie named jwtCookie in the request.
        * It stores the result in a Cookie object.
        */
        Cookie cookie = WebUtils.getCookie(request, jwtCookie);
        // Checks if the cookie was found (i.e., it's not null).
        if(cookie != null) {
            // If the cookie exists, it returns the value stored in the cookie (which is the JWT).
            return cookie.getValue();
        } else {
            // If the cookie doesn't exist, return null.
            return null;
        }
    }

    /*
    * Declares a public method named generateJwtCookie that takes a UserDetailsImpl
    * object as a parameter (this object likely contains user info).
    * It returns a ResponseCookie, which is used in Spring to represent
    * a cookie to be sent in the HTTP response.
    */
    public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal){
        /*
        * Generates a JWT token using the username of the logged-in user (via generateTokenFromUsername() method).
        */
        String jwt = generateTokenFromUsername(userPrincipal.getUsername());
        /*
        * Creates a new cookie with the name jwtCookie and value as the generated JWT.
        * This is done using ResponseCookie.from() builder.
        */
        ResponseCookie cookie = ResponseCookie.from(jwtCookie, jwt)
                .path("/api") // Sets the path where the cookie is valid (here it's /api). The browser will only send this cookie for requests under this path.
                .maxAge(24 * 60 * 60) // Sets the cookie expiration time to 24 hours (24 hours × 60 minutes × 60 seconds).
                .httpOnly(false) // Sets the cookie as not HTTP-only, meaning it can be accessed by JavaScript (which might be a security risk if not handled carefully).
                .build(); // Finalizes the construction of the ResponseCookie object.

        return cookie; // Returns the built cookie.

    }

    /*
    * This method is public and returns a ResponseCookie.
    * The purpose of this method is to generate a "clean" or empty JWT cookie — basically, a way to remove the JWT from the client.
    */
    public ResponseCookie getCleanJwtCookie(){
        /*
        * ResponseCookie.from(...) creates a new cookie builder.
        * jwtCookie is the cookie name (e.g., "jwt").
        * null is the cookie value, meaning we’re clearing the content (removing the token).*/
        ResponseCookie cookie = ResponseCookie.from(jwtCookie, null)
                /*
                * This sets the path for which the cookie is valid.
                * The browser will send this cookie only for requests that start with /api.
                */
                .path("/api")
                .build(); // This finalizes the cookie and creates a ResponseCookie object.
        return cookie; // Returns the built cookie.
    }

    // generating token from username
    /*
    * This method accepts a UserDetails object (usually from Spring Security) and returns a JWT token as a String.
    */
    public String generateTokenFromUsername(String username){

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
