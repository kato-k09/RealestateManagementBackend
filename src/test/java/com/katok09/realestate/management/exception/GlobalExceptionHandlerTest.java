package com.katok09.realestate.management.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler sut;

  @Mock
  private WebRequest webRequest;

  @BeforeEach
  void before() {
    sut = new GlobalExceptionHandler();
    when(webRequest.getDescription(false)).thenReturn("uri=/test");
  }

  @Test
  void BadCredentialsExceptionが適切にハンドリングされること() {

    BadCredentialsException exception = new BadCredentialsException("DummyMessage");

    ResponseEntity<Map<String, Object>> actual = sut.handleBadCredentials(exception, webRequest);

    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    Map<String, Object> body = actual.getBody();
    assertThat(body.get("error")).isEqualTo(true);
    assertThat(body.get("status")).isEqualTo(401);
    assertThat(body.get("errorCode")).isEqualTo("AUTHENTICATION_ERROR");
    assertThat(body.get("message")).isEqualTo("DummyMessage");
    assertThat(body.get("path")).isEqualTo("uri=/test");
    assertThat(body.get("timestamp")).isNotNull();
  }

  @Test
  void AuthenticationExceptionが適切にハンドリングされること() {

    AuthenticationException exception = new AuthenticationException("DummyMessage") {
    };

    ResponseEntity<Map<String, Object>> actual = sut.handlerAuthentication(exception, webRequest);

    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    Map<String, Object> body = actual.getBody();
    assertThat(body.get("error")).isEqualTo(true);
    assertThat(body.get("status")).isEqualTo(401);
    assertThat(body.get("errorCode")).isEqualTo("AUTHENTICATION_ERROR");
    assertThat(body.get("message")).isEqualTo("認証に失敗しました");
    assertThat(body.get("path")).isEqualTo("uri=/test");
    assertThat(body.get("timestamp")).isNotNull();
  }

  @Test
  void LockedExceptionが適切にハンドリングされること() {

    LockedException exception = new LockedException("DummyMessage");

    ResponseEntity<Map<String, Object>> actual = sut.handlerLocked(exception, webRequest);

    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.LOCKED);
    Map<String, Object> body = actual.getBody();
    assertThat(body.get("error")).isEqualTo(true);
    assertThat(body.get("status")).isEqualTo(423);
    assertThat(body.get("errorCode")).isEqualTo("ACCOUNT_LOCKED");
    assertThat(body.get("message")).isEqualTo("DummyMessage");
    assertThat(body.get("path")).isEqualTo("uri=/test");
    assertThat(body.get("timestamp")).isNotNull();
  }

  @Test
  void AccessDeniedExceptionが適切にハンドリングされること() {

    AccessDeniedException exception = new AccessDeniedException("DummyMessage");

    ResponseEntity<Map<String, Object>> actual = sut.handlerAccessDenied(exception, webRequest);

    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    Map<String, Object> body = actual.getBody();
    assertThat(body.get("error")).isEqualTo(true);
    assertThat(body.get("status")).isEqualTo(403);
    assertThat(body.get("errorCode")).isEqualTo("ACCESS_DENIED");
    assertThat(body.get("message")).isEqualTo("この操作を実行する権限がありません");
    assertThat(body.get("path")).isEqualTo("uri=/test");
    assertThat(body.get("timestamp")).isNotNull();
  }

  @Test
  void IllegalArgumentExceptionが適切にハンドリングされること() {

    IllegalArgumentException exception = new IllegalArgumentException("DummyMessage");

    ResponseEntity<Map<String, Object>> actual = sut.handlerIllegalArgument(exception, webRequest);

    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    Map<String, Object> body = actual.getBody();
    assertThat(body.get("error")).isEqualTo(true);
    assertThat(body.get("status")).isEqualTo(400);
    assertThat(body.get("errorCode")).isEqualTo("VALIDATION_ERROR");
    assertThat(body.get("message")).isEqualTo("DummyMessage");
    assertThat(body.get("path")).isEqualTo("uri=/test");
    assertThat(body.get("timestamp")).isNotNull();
  }

  @Test
  void MethodArgumentNotValidExceptionが適切にハンドリングされること() {

    MethodParameter methodParameter = mock(MethodParameter.class);
    BindingResult bindingResult = mock(BindingResult.class);
    FieldError fieldError1 = new FieldError("Object", "username", "ユーザー名は必須です。");
    FieldError fieldError2 = new FieldError("Object", "password", "パスワードは必須です。");
    when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));

    MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter,
        bindingResult) {
    };

    ResponseEntity<Map<String, Object>> actual = sut.handlerMethodArgumentNotValid(exception,
        webRequest);

    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    Map<String, Object> body = actual.getBody();
    assertThat(body.get("error")).isEqualTo(true);
    assertThat(body.get("status")).isEqualTo(400);
    assertThat(body.get("errorCode")).isEqualTo("VALIDATION_ERROR");
    assertThat(body.get("message")).isEqualTo("ユーザー名は必須です。\nパスワードは必須です。");
    assertThat(body.get("path")).isEqualTo("uri=/test");
    assertThat(body.get("timestamp")).isNotNull();
  }

  @Test
  void MethodArgumentNotValidExceptionのメッセージが空の時適切にハンドリングされること() {

    MethodParameter methodParameter = mock(MethodParameter.class);
    BindingResult bindingResult = mock(BindingResult.class);
    when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList());

    MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter,
        bindingResult) {
    };

    ResponseEntity<Map<String, Object>> actual = sut.handlerMethodArgumentNotValid(exception,
        webRequest);

    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    Map<String, Object> body = actual.getBody();
    assertThat(body.get("error")).isEqualTo(true);
    assertThat(body.get("status")).isEqualTo(400);
    assertThat(body.get("errorCode")).isEqualTo("VALIDATION_ERROR");
    assertThat(body.get("message")).isEqualTo("");
    assertThat(body.get("path")).isEqualTo("uri=/test");
    assertThat(body.get("timestamp")).isNotNull();
  }

  @ParameterizedTest
  @CsvSource({
      "DummyMessage, データの登録・更新に失敗しました",
      "username, このユーザー名は既に使用されています",
      "email, このメールアドレスは既に使用されています"
  })
  void DataIntegrityViolationExceptionが適切にハンドリングされること(String setMessage,
      String resultMessage) {

    DataIntegrityViolationException exception = new DataIntegrityViolationException(setMessage);

    ResponseEntity<Map<String, Object>> actual = sut.handlerDataIntegrityViolation(exception,
        webRequest);

    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    Map<String, Object> body = actual.getBody();
    assertThat(body.get("error")).isEqualTo(true);
    assertThat(body.get("status")).isEqualTo(409);
    assertThat(body.get("errorCode")).isEqualTo("DATA_INTEGRITY_ERROR");
    assertThat(body.get("message")).isEqualTo(resultMessage);
    assertThat(body.get("path")).isEqualTo("uri=/test");
    assertThat(body.get("timestamp")).isNotNull();
  }

  @Test
  void ResourceNotFoundExceptionが適切にハンドリングされること() {

    ResourceNotFoundException exception = new ResourceNotFoundException("DummyMessage");

    ResponseEntity<Map<String, Object>> actual = sut.handlerResourceNotFound(exception, webRequest);

    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    Map<String, Object> body = actual.getBody();
    assertThat(body.get("error")).isEqualTo(true);
    assertThat(body.get("status")).isEqualTo(404);
    assertThat(body.get("errorCode")).isEqualTo("RESOURCE_NOT_FOUND");
    assertThat(body.get("message")).isEqualTo("DummyMessage");
    assertThat(body.get("path")).isEqualTo("uri=/test");
    assertThat(body.get("timestamp")).isNotNull();
  }

  @Test
  void RuntimeExceptionが適切にハンドリングされること() {

    RuntimeException exception = new RuntimeException("DummyMessage");

    ResponseEntity<Map<String, Object>> actual = sut.handlerRuntimeException(exception, webRequest);

    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    Map<String, Object> body = actual.getBody();
    assertThat(body.get("error")).isEqualTo(true);
    assertThat(body.get("status")).isEqualTo(500);
    assertThat(body.get("errorCode")).isEqualTo("RUNTIME_ERROR");
    assertThat(body.get("message")).isEqualTo("予期しないエラーが発生しました");
    assertThat(body.get("path")).isEqualTo("uri=/test");
    assertThat(body.get("timestamp")).isNotNull();
  }

  @Test
  void Exceptionが適切にハンドリングされること() {

    Exception exception = new Exception("DummyMessage");

    ResponseEntity<Map<String, Object>> actual = sut.handlerGeneral(exception, webRequest);

    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    Map<String, Object> body = actual.getBody();
    assertThat(body.get("error")).isEqualTo(true);
    assertThat(body.get("status")).isEqualTo(500);
    assertThat(body.get("errorCode")).isEqualTo("INTERNAL_ERROR");
    assertThat(body.get("message")).isEqualTo("システムエラーが発生しました");
    assertThat(body.get("path")).isEqualTo("uri=/test");
    assertThat(body.get("timestamp")).isNotNull();
  }
}
