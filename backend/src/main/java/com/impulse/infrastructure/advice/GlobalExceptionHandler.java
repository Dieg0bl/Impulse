package com.impulse.infrastructure.advice;

import com.impulse.shared.error.DomainException;
import com.impulse.shared.error.ErrorCodes;
import com.impulse.shared.utils.CorrelationId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/** Translates exceptions into homogeneous error model {code,message,correlationId} */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Map<String,Object>> onDomain(DomainException ex) {
        return build(ex.getCode(), ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> onValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream().findFirst()
                .map(fe -> fe.getField()+": "+fe.getDefaultMessage()).orElse("Validation error");
        return build(ErrorCodes.VALIDATION_ERROR, msg, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> onGeneric(Exception ex) {
        return build("INTERNAL_ERROR", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Map<String,Object>> build(String code, String message, HttpStatus status) {
        String corr = CorrelationId.getOrGenerate();
        return ResponseEntity.status(status).body(Map.of(
                "code", code,
                "message", message,
                "correlationId", corr
        ));
    }
}
