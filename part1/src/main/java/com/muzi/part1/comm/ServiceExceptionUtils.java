package com.muzi.part1.comm;


public class ServiceExceptionUtils {
    public static com.muzi.part1.comm.ServiceException exception(String message, String code) {
        return new com.muzi.part1.comm.ServiceException(message, code);
    }

    public static com.muzi.part1.comm.ServiceException exception(String message) {
        return exception(message, null);
    }

    public static void throwException(String message, String code) {
        throw exception(message, code);
    }

    public static void throwException(String message) {
        throwException(message, null);
    }

}
