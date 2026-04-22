package com.nimbachi.banco_app.infraestructure.output.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.ApiResponse;

import org.springframework.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    //Metodo para obtener el path de la solicitud desde WebRequest
    private String getRequestPath(WebRequest request) {
        return ((ServletWebRequest) request).getRequest().getRequestURI();
    }

    // Manejo de excepciones de validación
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        FieldError fieldError = ex.getBindingResult().getFieldErrors().get(0);
        String message = fieldError.getDefaultMessage();
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ApiResponse<Object> apiResponse = ApiResponse.error(
                message,
                "VALIDATION_ERROR",
                status,
                getRequestPath(request));

        return new ResponseEntity<>(apiResponse, status);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException ex, WebRequest request) {
        log.warn("RuntimeException: {} at path {}", ex.getMessage(), getRequestPath(request));
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiResponse<Object> apiResponse = ApiResponse.error(
                ex.getMessage(),
                "BUSINESS_LOGIC_ERROR",
                status,
                getRequestPath(request));
        return new ResponseEntity<>(apiResponse, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex, WebRequest request) {
        log.error("An unexpected error occurred at path {}: ", getRequestPath(request), ex);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiResponse<Object> apiResponse = ApiResponse.error(
                "Ocurrió un error inesperado en el servidor. Por favor, contacte a soporte.",
                "INTERNAL_SERVER_ERROR",
                status,
                getRequestPath(request));
        return new ResponseEntity<>(apiResponse, status);
    }
}
