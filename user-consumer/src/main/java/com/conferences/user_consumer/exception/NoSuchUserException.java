package com.conferences.user_consumer.exception;

public class NoSuchUserException extends RuntimeException{
    public NoSuchUserException(){
        super("No User found");
    }
}
