package com.ecommerce.project.exceptions;

// custom error response whenever an exception happens

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // specialized version of controller advice ( this will intercept any exception that are thrown by any controller ) (validation)
public class MyGlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class) // This tells Spring: If someone gives invalid input to an API (like missing or bad data), then run the method below
    public ResponseEntity<Map<String, String>> myMethodArgumentNotValidException(MethodArgumentNotValidException e){

        // temp stored the field and message in hashmap
        Map<String, String> response = new HashMap<>();

        /*
        * This goes through all the errors (for example: "username is blank", "email is invalid").
        * It gets the name of the field (like username, email)
        * It gets the error message (like "must not be blank")
        * It adds them to the response map
        */
        e.getBindingResult().getAllErrors().forEach(err -> {
            String fieldName = ((FieldError)err).getField();
            String message = err.getDefaultMessage();
            response.put(fieldName, message);
        });

        return new ResponseEntity<Map<String, String>>(response,
                HttpStatus.BAD_REQUEST); // This sends back the list of errors in a clean, simple format, also a response entity.
    }

    // method for custom not found exception
    @ExceptionHandler(ResourceNotFoundException.class) // This tells Spring Boot: "If a ResourceNotFoundException happens, run the method below."
    public ResponseEntity<String> myResourceNotFoundException(ResourceNotFoundException e){ // ResourceNotFoundException is a custom error class we created (This is the method that will run when that error happens.)
        String message = e.getMessage(); // This gets the message from the exception

        /*
        * This sends back:
        * The message as the response body.
        * A status code: 404 NOT FOUND (which means "the thing you're looking for doesn't exist").
        * */
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);

    }


    @ExceptionHandler(APIException.class) // This tells Spring Boot: “If an error of type APIException is thrown anywhere, use this method to handle it.”
    public ResponseEntity<String> myAPIException(APIException e){
        /*
        * This grabs the message that you set when you threw the exception.
        * throw new APIException("You can't access this resource");
        * hen e.getMessage() will give: "You can't access this resource"
        */
        String message = e.getMessage();

        /*
        * This returns a response back to the client.
        * It includes:
        * The error message (message)
        * An HTTP status code: 400 BAD REQUEST, which means “the request was wrong or not allowed.”
        */
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);

    }
}
