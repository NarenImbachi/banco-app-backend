package com.nimbachi.banco_app.infraestructure.input.rest.dto.response;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private final boolean success;
    private final String message;
    private final String code;
    private final T data;
    private final Integer status;
    private final String path;

    private ApiResponse(boolean success, String message, String code, T data) {
        this.success = success;
        this.message = message;
        this.code = code;
        this.data = data;
        this.status = null;
        this.path = null;
    }

    private ApiResponse(boolean success, String message, String code, Integer status, String path) {
        this.success = success;
        this.message = message;
        this.code = code;
        this.data = null;
        this.status = status;
        this.path = path;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Operación exitosa.", "OK", data);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, "OK", data);
    }

    public static <T> ApiResponse<T> successEmpty(String message, String code) {
        return new ApiResponse<>(true, message, code, null);
    }

    public static <T> ApiResponse<T> error(String message, String code, HttpStatus status, String path) {
        return new ApiResponse<>(false, message, code, status.value(), path);
    }
}
