package com.culinaryapi.Order_Service.exception;


public class BadRequestException extends RuntimeException{

    public BadRequestException(String message) {
        super(message);
    }
}