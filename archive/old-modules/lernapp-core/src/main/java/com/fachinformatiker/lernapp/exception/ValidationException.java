package com.fachinformatiker.lernapp.exception;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends RuntimeException {
    
    private Map<String, String> errors = new HashMap<>();
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String field, String message) {
        super(message);
        this.errors.put(field, message);
    }
    
    public ValidationException(Map<String, String> errors) {
        super("Validation failed for multiple fields");
        this.errors = errors;
    }
    
    public Map<String, String> getErrors() {
        return errors;
    }
    
    public void addError(String field, String message) {
        errors.put(field, message);
    }
    
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}