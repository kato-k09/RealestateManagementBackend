package com.katok09.realestate.management.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class LoginRequestTest {

  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void ユーザー名とパスワードが設定された時入力チェックに異常が発生しないこと() {
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setUsername("DummyUser");
    loginRequest.setPassword("DummyPassword");

    Set<ConstraintViolation<LoginRequest>> actual = validator.validate(loginRequest);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void ユーザー名がnullの時入力チェックに異常が発生すること() {
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setPassword("DummyPassword");

    Set<ConstraintViolation<LoginRequest>> actual = validator.validate(loginRequest);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message").containsOnly("ユーザー名を入力してください。");
  }

  @Test
  void ユーザー名が空の時入力チェックに異常が発生すること() {
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setUsername("");
    loginRequest.setPassword("DummyPassword");

    Set<ConstraintViolation<LoginRequest>> actual = validator.validate(loginRequest);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message").containsOnly("ユーザー名を入力してください。");
  }

  @Test
  void パスワードがnullの時入力チェックに異常が発生すること() {
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setUsername("DummyUser");

    Set<ConstraintViolation<LoginRequest>> actual = validator.validate(loginRequest);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message").containsOnly("パスワードを入力してください。");
  }

  @Test
  void パスワードが空の時入力チェックに異常が発生すること() {
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setUsername("DummyUser");
    loginRequest.setPassword("");

    Set<ConstraintViolation<LoginRequest>> actual = validator.validate(loginRequest);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message").containsOnly("パスワードを入力してください。");
  }

}
