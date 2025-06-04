package com.finverse.auth.exception;

public class UserAlreadyExistsException extends RuntimeException{
    public String UserAlreadyExistsException() { return "User already exists with this username." ;}
}
