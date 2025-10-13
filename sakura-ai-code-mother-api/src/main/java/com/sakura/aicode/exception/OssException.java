package com.sakura.aicode.exception;


import com.sakura.aicode.common.ErrorCode;
import lombok.Getter;

/**
 * 处理Oss异常
 */
@Getter
public class OssException extends RuntimeException {

    /**
     * 错误码
     */
    private final int code;

    public OssException(int code, String message) {
        super(message);
        this.code = code;
    }

    public OssException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public OssException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

}