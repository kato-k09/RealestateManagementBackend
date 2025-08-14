package com.katok09.realestate.management.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class RegisterRequestTest {

  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void setterとgetterが正常に動作すること() {
    RegisterRequest actual = new RegisterRequest();
    actual.setUsername("DummyUser");
    actual.setPassword("DummyPassword");
    actual.setEmail("dummy@example.com");
    actual.setDisplayName("DummyUser");

    assertThat(actual.getUsername()).isEqualTo("DummyUser");
    assertThat(actual.getPassword()).isEqualTo("DummyPassword");
    assertThat(actual.getEmail()).isEqualTo("dummy@example.com");
    assertThat(actual.getDisplayName()).isEqualTo("DummyUser");
  }

  @Test
  void 各フィールドに正常な値が設定された時入力チェックに異常が発生しないこと() {
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setUsername("DummyUser");
    registerRequest.setPassword("DummyPassword");
    registerRequest.setEmail("dummy@example.com");
    registerRequest.setDisplayName("DummyUser");

    Set<ConstraintViolation<RegisterRequest>> actual = validator.validate(registerRequest);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void ユーザー名がnullの時入力チェックに異常が発生すること() {
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setPassword("DummyPassword");
    registerRequest.setEmail("dummy@example.com");
    registerRequest.setDisplayName("DummyUser");

    Set<ConstraintViolation<RegisterRequest>> actual = validator.validate(registerRequest);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message").containsOnly("ユーザー名を入力してください。");
  }

  @Test
  void ユーザー名が空の時入力チェックに異常が発生すること() {
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setUsername("");
    registerRequest.setPassword("DummyPassword");
    registerRequest.setEmail("dummy@example.com");
    registerRequest.setDisplayName("DummyUser");

    Set<ConstraintViolation<RegisterRequest>> actual = validator.validate(registerRequest);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message").containsOnly("ユーザー名を入力してください。");
  }

  @Test
  void パスワードがnullの時入力チェックに異常が発生すること() {
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setUsername("DummyUser");
    registerRequest.setEmail("dummy@example.com");
    registerRequest.setDisplayName("DummyUser");

    Set<ConstraintViolation<RegisterRequest>> actual = validator.validate(registerRequest);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message").containsOnly("パスワードを入力してください。");
  }

  @Test
  void パスワードが空の時入力チェックに異常が発生すること() {
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setUsername("DummyUser");
    registerRequest.setPassword("");
    registerRequest.setEmail("dummy@example.com");
    registerRequest.setDisplayName("DummyUser");

    Set<ConstraintViolation<RegisterRequest>> actual = validator.validate(registerRequest);

    assertThat(actual.size()).isEqualTo(2);
    assertThat(actual).extracting("message")
        .containsOnly("パスワードを入力してください。", "パスワードは6文字以上で入力してください。");
  }

  @Test
  void パスワードが5文字の時入力チェックに異常が発生すること() {
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setUsername("DummyUser");
    registerRequest.setPassword("12345");
    registerRequest.setEmail("dummy@example.com");
    registerRequest.setDisplayName("DummyUser");

    Set<ConstraintViolation<RegisterRequest>> actual = validator.validate(registerRequest);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("パスワードは6文字以上で入力してください。");
  }

  @Test
  void パスワードが6文字の時入力チェックに異常が発生しないこと() {
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setUsername("DummyUser");
    registerRequest.setPassword("123456");
    registerRequest.setEmail("dummy@example.com");
    registerRequest.setDisplayName("DummyUser");

    Set<ConstraintViolation<RegisterRequest>> actual = validator.validate(registerRequest);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void Emailがnullの時入力チェックに異常が発生すること() {
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setUsername("DummyUser");
    registerRequest.setPassword("DummyPassword");
    registerRequest.setDisplayName("DummyUser");

    Set<ConstraintViolation<RegisterRequest>> actual = validator.validate(registerRequest);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message").containsOnly("メールアドレスを入力してください。");
  }

  @Test
  void Emailが空の時入力チェックに異常が発生すること() {
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setUsername("DummyUser");
    registerRequest.setPassword("DummyPassword");
    registerRequest.setEmail("");
    registerRequest.setDisplayName("DummyUser");

    Set<ConstraintViolation<RegisterRequest>> actual = validator.validate(registerRequest);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message").containsOnly("メールアドレスを入力してください。");
  }

  @Test
  void Email形式でない値を設定した時入力チェックに異常が発生すること() {
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setUsername("DummyUser");
    registerRequest.setPassword("DummyPassword");
    registerRequest.setEmail("abc");
    registerRequest.setDisplayName("DummyUser");

    Set<ConstraintViolation<RegisterRequest>> actual = validator.validate(registerRequest);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("有効なメールアドレスを入力してください。");
  }

  @Test
  void 表示名がnullの時入力チェックに異常が発生すること() {
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setUsername("DummyUser");
    registerRequest.setPassword("DummyPassword");
    registerRequest.setEmail("dummy@example.com");

    Set<ConstraintViolation<RegisterRequest>> actual = validator.validate(registerRequest);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message").containsOnly("表示名を入力してください。");
  }

  @Test
  void 表示名が空の時入力チェックに異常が発生すること() {
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setUsername("DummyUser");
    registerRequest.setPassword("DummyPassword");
    registerRequest.setEmail("dummy@example.com");

    Set<ConstraintViolation<RegisterRequest>> actual = validator.validate(registerRequest);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message").containsOnly("表示名を入力してください。");
  }

}
