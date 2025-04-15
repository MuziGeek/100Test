package com.muzi.part4.concurrencysafe;


public class ConcurrencyFailException extends RuntimeException {
    private String key;

    public ConcurrencyFailException(String message, String key) {
        super(message);
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
