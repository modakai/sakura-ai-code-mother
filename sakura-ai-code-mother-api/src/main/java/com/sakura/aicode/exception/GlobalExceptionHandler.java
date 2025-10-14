package com.sakura.aicode.exception;

import com.sakura.aicode.common.BaseResponse;
import com.sakura.aicode.common.ErrorCode;
import com.sakura.aicode.common.ResultUtils;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 *
 * @author sakura
 * @from sakura
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理请求体校验失败
     *
     * @param ex 异常
     * @return Result
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<?> handleValidationExceptions(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        // 从errors中转换错误信息为字符串
        StringBuilder messageBuilder = new StringBuilder();
        errors.forEach((field, msg) -> {
            if (!messageBuilder.isEmpty()) {
                messageBuilder.append("；"); // 用分号分隔多个错误
            }
            messageBuilder.append(field).append("：").append(msg);
        });
        // 兜底消息，防止空异常
        String message = !messageBuilder.isEmpty() ? messageBuilder.toString() : "请求体参数校验失败";

        return ResultUtils.error(ErrorCode.PARAMS_ERROR, message);
    }

    /**
     * 处理路径变量或请求参数校验失败
     *
     * @param ex 异常
     * @return Result
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public BaseResponse<?> handleConstraintViolationExceptions(ConstraintViolationException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            // 处理参数路径，例如"methodName.paramName"简化为"paramName"
            String fieldName = violation.getPropertyPath().toString();
            if (fieldName.contains(".")) {
                fieldName = fieldName.substring(fieldName.lastIndexOf(".") + 1);
            }
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        });

        // 从errors中转换错误信息为字符串
        StringBuilder messageBuilder = new StringBuilder();
        errors.forEach((field, msg) -> {
            if (!messageBuilder.isEmpty()) {
                messageBuilder.append("；"); // 用分号分隔多个错误
            }
            messageBuilder.append(field).append("：").append(msg);
        });
        // 兜底消息，防止空异常
        String message = !messageBuilder.isEmpty() ? messageBuilder.toString() : "路径参数或请求参数校验失败";

        return ResultUtils.error(ErrorCode.PARAMS_ERROR, message);
    }

    @ExceptionHandler(OssException.class)
    public BaseResponse<?> businessExceptionHandler(OssException e) {
        log.error("OssException", e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }


    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统错误");
    }
}
