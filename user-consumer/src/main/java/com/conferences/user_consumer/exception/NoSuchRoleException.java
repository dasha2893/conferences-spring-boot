package com.conferences.user_consumer.exception;

public class NoSuchRoleException extends RuntimeException {
    public NoSuchRoleException(){
        super("No such Role");
    }
}
