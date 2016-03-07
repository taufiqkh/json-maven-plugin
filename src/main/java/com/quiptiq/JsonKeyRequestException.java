package com.quiptiq;

/**
 * Denotes an exception due to a request for a key that does not have a simple value
 */
public class JsonKeyRequestException extends Exception {
    public JsonKeyRequestException(String message){
        super(message);
    }

    public JsonKeyRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
