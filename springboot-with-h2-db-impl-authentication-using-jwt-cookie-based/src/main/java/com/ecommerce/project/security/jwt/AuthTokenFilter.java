package com.ecommerce.project.security.jwt;

import com.ecommerce.project.security.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// This means that AuthTokenFilter is a custom filter that runs once for every HTTP request that hits your server
@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    // came from the jwt utils we created
    @Autowired
    private JwtUtils jwtUtils;

    // UserDetailsServiceImpl is custom we created
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

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
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    /*
    * This is the method inside your custom filter class (AuthTokenFilter) that Spring calls every time a request is made to your backend.
    */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //  Logs which URL is being accessed. Good for debugging.
        logger.debug("AuthTokenFilter called for URI: {}", request.getRequestURI());

        try{
            // Extracts the token from the Authorization header
            String jwt = parseJwt(request);
            /*
            * If the JWT exists.
            * If it's valid (not expired, not tampered with).
            * */
            if(jwt != null && jwtUtils.validateJwtToke(jwt)){
                //  Gets the username from the token’s payload
                String username = jwtUtils.getUsernameFromJWTToken(jwt);
                // Loads full user details from the database , such as roles.
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                /*
                * Creates an authentication object containing:
                * The user
                * No password (because we already verified the token)
                * The user's roles/permissions
                */
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                // Adds extra request info (like IP address, session ID) into the authentication object.
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // From now on, Spring will treat this user as logged in for this request.
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.debug("Roles from JWT: {}", userDetails.getAuthorities());
            }
        }catch (Exception e){
            logger.error("Cannot set user authentication: {}", e.getMessage());

        }
        //  Moves the request forward to the next filter or controller. This must always be called at the end of a filter.
        filterChain.doFilter(request, response);
    }

    // create a method to extract the JWT token from the HTTP request.
    private String parseJwt(HttpServletRequest request) {
        /*
        * jwtUtils.getJwtFromCookies(request) is a helper method (from your JwtUtils class).
        * It reads the cookie from the request.
        */
        String jwt = jwtUtils.getJwtFromCookies(request);
        logger.debug("AuthTokenFilter.java: {}", jwt); // This logs the token to the console (for debugging).
        return jwt; // Finally, it returns the JWT token (or null if no token was found).
    }
}
