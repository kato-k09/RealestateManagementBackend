package com.katok09.realestate.management.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException e,
      WebRequest request) {
    return createErrorResponse(
        HttpStatus.UNAUTHORIZED,
        "AUTHENTICATION_ERROR",
        e.getMessage(),
        request.getDescription(false)
    );
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<Map<String, Object>> handlerAuthentication(AuthenticationException e,
      WebRequest request) {
    return createErrorResponse(
        HttpStatus.UNAUTHORIZED,
        "AUTHENTICATION_ERROR",
        "認証に失敗しました",
        request.getDescription(false)
    );
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Map<String, Object>> handlerAccessDenied(AccessDeniedException e,
      WebRequest request) {
    return createErrorResponse(
        HttpStatus.FORBIDDEN,
        "ACCESS_DENIED",
        "この操作を実行する権限がありません",
        request.getDescription(false)
    );
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handlerIllegalArgument(IllegalArgumentException e,
      WebRequest request) {
    return createErrorResponse(
        HttpStatus.BAD_REQUEST,
        "VALIDATION_ERROR",
        e.getMessage(),
        request.getDescription(false)
    );
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(
      MethodArgumentNotValidException e, WebRequest request) {
    String errorMessage = e.getBindingResult().getFieldErrors().stream()
        .map(FieldError::getDefaultMessage)
        .collect(Collectors.joining("\n"));

    return createErrorResponse(
        HttpStatus.BAD_REQUEST,
        "VALIDATION_ERROR",
        errorMessage,
        request.getDescription(false)
    );
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Map<String, Object>> handlerDataIntegrityViolation(
      DataIntegrityViolationException e,
      WebRequest request) {

    String message = "データの登録・更新に失敗しました";

    if (e.getMessage() != null) {
      if (e.getMessage().contains("username")) {
        message = "このユーザー名は既に使用されています";
      } else if (e.getMessage().contains("email")) {
        message = "このメールアドレスは既に使用されています";
      }
    }

    return createErrorResponse(
        HttpStatus.CONFLICT,
        "DATA_INTEGRITY_ERROR",
        message,
        request.getDescription(false)
    );
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handlerResourceNotFound(ResourceNotFoundException e,
      WebRequest request) {
    return createErrorResponse(
        HttpStatus.NOT_FOUND,
        "RESOURCE_NOT_FOUND",
        e.getMessage(),
        request.getDescription(false)
    );
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<Map<String, Object>> handlerRuntimeException(RuntimeException e,
      WebRequest request) {
    return createErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "RUNTIME_ERROR",
        "予期しないエラーが発生しました",
        request.getDescription(false)
    );
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handlerGeneral(Exception e, WebRequest request) {
    return createErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "INTERNAL_ERROR",
        "システムエラーが発生しました",
        request.getDescription(false)
    );
  }

  private ResponseEntity<Map<String, Object>> createErrorResponse(
      HttpStatus status,
      String errorCode,
      String message,
      String path) {

    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("error", true);
    errorResponse.put("status", status.value());
    errorResponse.put("errorCode", errorCode);
    errorResponse.put("message", message);
    errorResponse.put("path", path);
    errorResponse.put("timestamp", System.currentTimeMillis());

    return new ResponseEntity<>(errorResponse, status);
  }
}

class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException(String message) {
    super(message);
  }
}