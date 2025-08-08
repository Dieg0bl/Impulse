
package com.impulse.common.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Constantes para claves de error y mensaje en respuestas de error.
 */
final class ErrorConstants {
    static final String ERROR = "error";
    static final String MESSAGE = "message";
    private ErrorConstants() {}
}

/**
 * Manejador global de excepciones REST.
 * Cumple compliance: auditabilidad, trazabilidad, REST, RGPD.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(ConflictException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put(ErrorConstants.ERROR, "conflict");
        body.put(ErrorConstants.MESSAGE, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put(ErrorConstants.ERROR, "not_found");
        body.put(ErrorConstants.MESSAGE, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put(ErrorConstants.ERROR, "bad_request");
        body.put(ErrorConstants.MESSAGE, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put(ErrorConstants.ERROR, "validation_error");
        body.put(ErrorConstants.MESSAGE, ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put(ErrorConstants.ERROR, "bad_request");
        body.put(ErrorConstants.MESSAGE, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put(ErrorConstants.ERROR, "internal_error");
        body.put(ErrorConstants.MESSAGE, "Error interno inesperado");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
