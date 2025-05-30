package com.ecommerce.project.exceptions;

// creating a class for ResourceNotFoundException for our global exception handler not found

/*
* You are creating a new type of error called ResourceNotFoundException.
* It extends RuntimeException, which means it's an error that can happen while the program is running
* */
public class ResourceNotFoundException extends RuntimeException {

    String resourceName;
    String field;
    String fieldName;
    Long fieldId;

    public ResourceNotFoundException() {
    }

    public ResourceNotFoundException(String resourceName, String field, String fieldName) {
        super(String.format("%s not found with %s: %s", resourceName, field, fieldName));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldName = fieldName;
    }

    public ResourceNotFoundException(String resourceName, String field, Long fieldId) {
        super(String.format("%s not found with %s: %d", resourceName, field, fieldId));
        this.resourceName = resourceName;
        this.field = field;

        this.fieldId = fieldId;
    }
}
