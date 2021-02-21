package com.dadobt.homework.exceptions;


public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String error){
        super(error);
    }

}
