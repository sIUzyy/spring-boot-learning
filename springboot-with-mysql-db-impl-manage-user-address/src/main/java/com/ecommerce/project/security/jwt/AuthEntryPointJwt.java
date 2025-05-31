package com.ecommerce.project.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// This class is a custom handler for unauthorized access in Spring Security.
// It tells Spring Security what to do when a user tries to access a protected resource without being logged in (or if their token is invalid).
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

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
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    /*
    * This is the method you’re overriding. It gets triggered when someone tries to access a protected resource but fails to authenticate.
    * request: the incoming HTTP request.
    * response: what you send back to the user.
    * authException: info about why the authentication failed.
    */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // This logs the error message (for developers/admins).
        logger.error("Unauthorized error: {}", authException.getMessage());

        /*
        * This tells the browser or client:
        * “Hey, the response I'm sending is in JSON format.”
        */
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // Sets the response status to 401 – the standard code for Unauthorized.
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Creates a map (like a mini-dictionary) to store the JSON data we want to send back.
        final Map<String, Object> body = new HashMap<>();
        // Adds a key-value pair to the response body: "status": 401
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        // Adds another key-value pair: "error": "Unauthorized"
        body.put("error", "Unauthorized");
        // Adds the error message from Spring Security: "message": "Full authentication is required to access this resource"
        body.put("message", authException.getMessage());
        // Adds the path of the endpoint the user tried to access: "path": "/api/secure-data"
        body.put("path", request.getServletPath());

        // Creates a tool (mapper) that can convert Java objects into JSON.
        final ObjectMapper mapper = new ObjectMapper();
        // Sends the body (the map you built) as JSON in the HTTP response.
        mapper.writeValue(response.getOutputStream(), body);

    }
}
