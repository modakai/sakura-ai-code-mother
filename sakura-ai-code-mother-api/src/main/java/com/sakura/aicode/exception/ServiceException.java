package com.sakura.aicode.exception;

public class ServiceException extends RuntimeException {
    /**
     * 错误提示
     */
    private final String message;

    public ServiceException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
