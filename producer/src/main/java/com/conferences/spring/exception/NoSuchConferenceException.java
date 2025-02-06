package com.conferences.spring.exception;

public class NoSuchConferenceException extends RuntimeException {
    public NoSuchConferenceException(){
        super("No such conference");
    }
}
