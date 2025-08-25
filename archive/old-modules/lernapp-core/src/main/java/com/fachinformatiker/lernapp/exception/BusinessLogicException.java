package com.fachinformatiker.lernapp.exception;

public class BusinessLogicException extends RuntimeException {
    
    private String errorCode;
    
    public BusinessLogicException(String message) {
        super(message);
    }
    
    public BusinessLogicException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public BusinessLogicException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public BusinessLogicException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}