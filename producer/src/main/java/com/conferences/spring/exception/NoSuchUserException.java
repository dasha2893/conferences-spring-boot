package com.conferences.spring.exception;

public class NoSuchUserException extends RuntimeException{
    public NoSuchUserException(){
        super("No User found");
    }
}
