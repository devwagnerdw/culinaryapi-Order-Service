package com.culinaryapi.Order_Service.exception;

public class HttpClientException extends RuntimeException{
    public HttpClientException(String message) {
        super(message);
    }
}