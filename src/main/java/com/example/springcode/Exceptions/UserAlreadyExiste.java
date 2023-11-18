package com.example.springcode.Exceptions;

public class UserAlreadyExiste extends RuntimeException{
    public UserAlreadyExiste(String message){
        super(message);
    }
}
