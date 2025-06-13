package com.finverse.lendingengine.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String userID) {
        super("User with id: "+userID+" not found.");
    }
}
