package com.chat.java.exception;



@SuppressWarnings("all")
public class CustomException extends RuntimeException {

    /*
    customMessages
     */
    private String message;


    /**
     * Instantiates a new Custom exception.
     *
     * @param message the message
     */
    public CustomException(final String message) {
        super(message);
        this.message = message;

    }
}
