package com.sakura.aicode.exception;


import com.sakura.aicode.common.ErrorCode;

/**
 * 抛异常工具类
 *
 * @author sakura
 * @from sakura
 */
public class ThrowUtils {

    /**
     * 条件成立则抛异常
     *
     * @param condition
     * @param runtimeException
     */
    public static void throwIf(boolean condition, RuntimeException runtimeException) {
        if (condition) {
            throw runtimeException;
        }
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition
     * @param errorCode
     */
    public static void throwIf(boolean condition, ErrorCode errorCode) {
        throwIf(condition, new BusinessException(errorCode));
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition
     * @param errorCode
     * @param message
     */
    public static void throwIf(boolean condition, ErrorCode errorCode, String message) {
        throwIf(condition, new BusinessException(errorCode, message));
    }

    /**
     * 校验id
     * @param id id
     * @param errorCode
     * @param message
     */
    public static void throwIfId(Number id, ErrorCode errorCode, String message) {
        throwIf(id == null || id.longValue() <= 0, new BusinessException(errorCode, message));
    }
}
