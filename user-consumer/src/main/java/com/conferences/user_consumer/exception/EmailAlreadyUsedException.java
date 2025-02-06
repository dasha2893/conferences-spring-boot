package com.conferences.user_consumer.exception;

public class EmailAlreadyUsedException extends RuntimeException{

    public EmailAlreadyUsedException(){
        super("User with such Email is already exist");
    }
}
