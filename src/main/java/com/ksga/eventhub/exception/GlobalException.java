package com.ksga.eventhub.exception;

import com.ksga.eventhub.model.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("type", "about:blank");
        error.put("title", "Validation Failed");
        error.put("status", 400);
        error.put("instance", request.getRequestURI());

        List<Map<String, String>> errors = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.of("field", fe.getField(), "message", fe.getDefaultMessage()))
                .collect(Collectors.toList());
        error.put("errors", errors);
        error.put("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException e, HttpServletRequest request) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("type", "about:blank");
        error.put("title", "Not Found");
        error.put("status", 404);
        error.put("detail", e.getMessage());
        error.put("instance", request.getRequestURI());
        error.put("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateUser(DuplicateUserException e, HttpServletRequest request) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("type", "about:blank");
        error.put("title", "Conflict");
        error.put("status", 409);
        error.put("detail", e.getMessage());
        error.put("instance", request.getRequestURI());
        error.put("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequestException(BadRequestException b, HttpServletRequest request) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("type", "about:blank");
        error.put("title", "Bad Request");
        error.put("status", 400);
        error.put("instance", request.getRequestURI());

        if (b.getErrors() != null && !b.getErrors().isEmpty()) {
            error.put("errors", b.getErrors());
        } else {
            error.put("detail", b.getMessage());
        }

        error.put("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception e, HttpServletRequest request) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("type", "about:blank");
        error.put("title", "Internal Server Error");
        error.put("status", 500);
        error.put("detail", "An unexpected error occurred.");
        error.put("instance", request.getRequestURI());
        error.put("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(
                ApiResponse.<Void>builder()
                        .success(false)
                        .message(ex.getReason())
                        .payload(null)
                        .build()
        );
    }
}
