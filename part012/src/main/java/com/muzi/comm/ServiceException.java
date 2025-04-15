package com.muzi.comm;


public class ServiceException extends  RuntimeException{
    private String code;

    public ServiceException(String message, String code) {
        super(message);
        this.code = code;
    }
}
