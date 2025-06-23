package com.yapily.openbanking_app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Map<String,Object>> handleClientError(HttpClientErrorException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(Map.of(
                        "status",  ex.getStatusCode().value(),
                        "error",   ex.getStatusText(),
                        "message", ex.getResponseBodyAsString()
                ));
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<Map<String,Object>> handleServerError(HttpServerErrorException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(Map.of(
                        "status",  ex.getStatusCode().value(),
                        "error",   ex.getStatusText(),
                        "message", "Upstream service error"
                ));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String,Object>> handleMissingParam(MissingServletRequestParameterException ex) {
        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "status",  HttpStatus.BAD_REQUEST.value(),
                        "error",   "Missing parameter",
                        "message", ex.getParameterName() + " parameter is required"
                ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String,Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String typeName = ex.getRequiredType() != null
                ? ex.getRequiredType().getSimpleName()
                : "unknown";
        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "status",  HttpStatus.BAD_REQUEST.value(),
                        "error",   "Type mismatch",
                        "message", String.format(
                                "Parameter '%s' should be of type %s",
                                ex.getName(),
                                typeName
                        )
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleAll(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "status",  HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "error",   "Internal error",
                        "message", ex.getMessage()
                ));
    }
}
