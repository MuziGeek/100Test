package com.muzi.part17.common;


public class BusinessExceptionUtils {
    /**
     * 创建 BusinessException
     *
     * @param code
     * @param msg
     * @return
     */
    public static BusinessException businessException(String code, String msg) {
        return new BusinessException(code, msg);
    }

    /**
     * 创建 BusinessException
     *
     * @param code
     * @param msg
     * @param cause
     * @return
     */
    public static BusinessException businessException(String code, String msg, Throwable cause) {
        return new BusinessException(code, msg, cause);
    }
}
