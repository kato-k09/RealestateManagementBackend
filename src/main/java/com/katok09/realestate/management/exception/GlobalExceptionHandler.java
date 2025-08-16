package com.katok09.realestate.management.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/**
 * アプリケーション全体の例外を一元管理するグローバル例外ハンドラー コントローラーで発生した例外を一元管理するクラス
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * ユーザー名、パスワードの間違いによる認証失敗例外をハンドリング 詳細なエラーメッセージを返します。
   *
   * @param e       BadCredentialsException
   * @param request WebRequest
   * @return 401 Unauthorizedエラーレスポンス
   */
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

  /**
   * 全般的な認証失敗例外をハンドリング
   *
   * @param e       AuthenticationException
   * @param request WebRequest
   * @return 401 Unauthorizedエラーレスポンス
   */
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

  /**
   * アカウントロックされているユーザーからのログイン失敗例外をハンドリング アカウントロック解除までの期間を分数で表しエラーメッセージを返します。
   *
   * @param e       LockedException
   * @param request WebRequest
   * @return 423 Lockedエラーレスポンス
   */
  @ExceptionHandler(LockedException.class)
  public ResponseEntity<Map<String, Object>> handlerLocked(LockedException e, WebRequest request) {
    return createErrorResponse(
        HttpStatus.LOCKED,
        "ACCOUNT_LOCKED",
        e.getMessage(),
        request.getDescription(false)
    );
  }

  /**
   * 実行権限の無い操作時の例外をハンドリング
   *
   * @param e       AccessDeniedException
   * @param request WebRequest
   * @return 403 Forbiddenエラーレスポンス
   */
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

  /**
   * ビジネスロジックによるバリデーションエラー時の例外をハンドリング 詳細なエラーメッセージを返します。
   *
   * @param e       IllegalArgumentException
   * @param request WebRequest
   * @return 400 Bad Requestエラーレスポンス
   */
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

  /**
   * アノテーションによるバリデーションエラー時の例外をハンドリング 詳細なエラーメッセージを返します。
   *
   * @param e       MethodArgumentNotValidException
   * @param request WebRequest
   * @return 400 Bad Requestエラーレスポンス
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handlerMethodArgumentNotValid(
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

  /**
   * データの登録・更新失敗時の例外をハンドリングします。 ユーザー名、パスワードが既に他のアカウントにより使用されている場合はその情報をエラーレスポンスとして返します。
   *
   * @param e       DataIntegrityViolationException
   * @param request WebRequest
   * @return 409 Conflictエラーレスポンス
   */
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

  /**
   * 予期しない全般的なエラー時の例外をハンドリング
   *
   * @param e       RuntimeException
   * @param request WebRequest
   * @return 500 Internal Server Errorエラーレスポンス
   */
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

  /**
   * 他の例外に当てはまらない全ての例外のハンドリングを行います。
   *
   * @param e       Exception
   * @param request WebRequest
   * @return 500 Internal Server Error
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handlerGeneral(Exception e, WebRequest request) {
    return createErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "INTERNAL_ERROR",
        "システムエラーが発生しました",
        request.getDescription(false)
    );
  }

  /**
   * 全ての例外で統一的なエラーレスポンスを生成します。
   *
   * @param status    httpステータス
   * @param errorCode エラーコード
   * @param message   エラーメッセージ
   * @param path      実行時パス
   * @return エラーレスポンス
   */
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
