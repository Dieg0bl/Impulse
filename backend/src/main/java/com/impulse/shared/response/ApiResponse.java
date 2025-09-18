package com.impulse.shared.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Standard API response wrapper for IMPULSE v1.0
 * Ensures consistent response format across all endpoints
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private String code;
    private String message;
    private String correlationId;
    private T data;
    private Object error;

    // Constructors
    public ApiResponse() {}

    private ApiResponse(String code, String message, T data, Object error, String correlationId) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.error = error;
        this.correlationId = correlationId;
    }

    // Factory methods for success responses
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("SUCCESS", "Operation completed successfully", data, null, null);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>("SUCCESS", message, data, null, null);
    }

    // Factory methods for error responses
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>("ERROR", message, null, null, null);
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(code, message, null, null, null);
    }

    public static <T> ApiResponse<T> error(String code, String message, Object error) {
        return new ApiResponse<>(code, message, null, error, null);
    }

    // Factory methods with correlation ID
    public static <T> ApiResponse<T> successWithCorrelation(T data, String correlationId) {
        return new ApiResponse<>("SUCCESS", "Operation completed successfully", data, null, correlationId);
    }

    public static <T> ApiResponse<T> errorWithCorrelation(String message, String correlationId) {
        return new ApiResponse<>("ERROR", message, null, null, correlationId);
    }

    // Getters and setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", correlationId='" + correlationId + '\'' +
                ", data=" + data +
                ", error=" + error +
                '}';
    }
}
