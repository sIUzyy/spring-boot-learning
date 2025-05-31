package com.ecommerce.project.exceptions;

// creating a new custom error called APIException that extends the RuntimeException.
public class APIException extends RuntimeException {

    /*
    * This line is usually added when a class extends Exception or RuntimeException.
    * It helps Java during the process of serialization (saving the object as data). It's not something you need to worry about much as a beginner.
    * You can think of it like an ID for this exception class.
    */
    private static final long serialVersionUID = 1L;

    public APIException() {
    }

    public APIException(String message) {
        super(message);
    }
}
