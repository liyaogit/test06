package com.hsbc.transaction.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 * 
 * @author HSBC Development Team
 * @version 1.0.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理交易未找到异常
     */
    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTransactionNotFoundException(TransactionNotFoundException ex) {
        logger.warn("交易未找到: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "交易未找到",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * 处理重复交易异常
     */
    @ExceptionHandler(DuplicateTransactionException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateTransactionException(DuplicateTransactionException ex) {
        logger.warn("重复交易: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "重复交易",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * 处理无效交易异常
     */
    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTransactionException(InvalidTransactionException ex) {
        logger.warn("无效交易: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "无效交易",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        logger.warn("参数验证失败: {}", ex.getMessage());
        
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "参数验证失败",
                "请检查输入参数",
                LocalDateTime.now(),
                validationErrors
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("系统异常: ", ex);
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "系统异常",
                "系统内部错误，请稍后重试",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 错误响应类
     */
    public static class ErrorResponse {
        private int status;
        private String error;
        private String message;
        private LocalDateTime timestamp;
        private Map<String, String> validationErrors;

        public ErrorResponse(int status, String error, String message, LocalDateTime timestamp) {
            this.status = status;
            this.error = error;
            this.message = message;
            this.timestamp = timestamp;
        }

        public ErrorResponse(int status, String error, String message, LocalDateTime timestamp, 
                           Map<String, String> validationErrors) {
            this(status, error, message, timestamp);
            this.validationErrors = validationErrors;
        }

        // Getters and Setters
        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }

        public Map<String, String> getValidationErrors() {
            return validationErrors;
        }

        public void setValidationErrors(Map<String, String> validationErrors) {
            this.validationErrors = validationErrors;
        }
    }
} 