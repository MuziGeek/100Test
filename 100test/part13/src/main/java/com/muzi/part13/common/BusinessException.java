package com.muzi.part13.common;


public class BusinessException extends RuntimeException {
    private String code;

    /**
     * @param code    错误编码
     * @param message 错误提示
     */
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
