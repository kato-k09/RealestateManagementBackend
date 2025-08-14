package com.katok09.realestate.management.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class UpdateRequestTest {

  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void setterとgetterが正常に動作すること() {
    UpdateRequest actual = new UpdateRequest();
    actual.setUsername("DummyUser");
    actual.setEmail("dummy@example.com");
    actual.setDisplayName("DummyUser");
    actual.setCurrentPassword("CurrentPassword");
    actual.setNewPassword("NewPassword");

    assertThat(actual.getUsername()).isEqualTo("DummyUser");
    assertThat(actual.getEmail()).isEqualTo("dummy@example.com");
    assertThat(actual.getDisplayName()).isEqualTo("DummyUser");
    assertThat(actual.getCurrentPassword()).isEqualTo("CurrentPassword");
    assertThat(actual.getNewPassword()).isEqualTo("NewPassword");
  }

  @Test
  void 各フィールドに正常な値が設定された時入力チェックに異常が発生しないこと() {
    UpdateRequest updateRequest = new UpdateRequest();
    updateRequest.setUsername("DummyUser");
    updateRequest.setEmail("dummy@example.com");
    updateRequest.setDisplayName("DummyUser");
    updateRequest.setCurrentPassword("CurrentPassword");
    updateRequest.setNewPassword("NewPassword");

    Set<ConstraintViolation<UpdateRequest>> actual = validator.validate(updateRequest);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void ユーザー名がnullの時入力チェックに異常が発生すること() {
    UpdateRequest updateRequest = new UpdateRequest();
    updateRequest.setEmail("dummy@example.com");
    updateRequest.setDisplayName("DummyUser");
    updateRequest.setCurrentPassword("CurrentPassword");
    updateRequest.setNewPassword("NewPassword");

    Set<ConstraintViolation<UpdateRequest>> actual = validator.validate(updateRequest);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message").containsOnly("ユーザー名を入力してください。");
  }

  @Test
  void ユーザー名が空の時入力チェックに異常が発生すること() {
    UpdateRequest updateRequest = new UpdateRequest();
    updateRequest.setUsername("");
    updateRequest.setEmail("dummy@example.com");
    updateRequest.setDisplayName("DummyUser");
    updateRequest.setCurrentPassword("CurrentPassword");
    updateRequest.setNewPassword("NewPassword");

    Set<ConstraintViolation<UpdateRequest>> actual = validator.validate(updateRequest);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message").containsOnly("ユーザー名を入力してください。");
  }

  @Test
  void Emailがnullの時入力チェックに異常が発生すること() {
    UpdateRequest updateRequest = new UpdateRequest();
    updateRequest.setUsername("DummyUser");
    updateRequest.setDisplayName("DummyUser");
    updateRequest.setCurrentPassword("CurrentPassword");
    updateRequest.setNewPassword("NewPassword");

    Set<ConstraintViolation<UpdateRequest>> actual = validator.validate(updateRequest);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message").containsOnly("メールアドレスを入力してください。");
  }

  @Test
  void Emailが空の時入力チェックに異常が発生すること() {
    UpdateRequest updateRequest = new UpdateRequest();
    updateRequest.setUsername("DummyUser");
    updateRequest.setEmail("");
    updateRequest.setDisplayName("DummyUser");
    updateRequest.setCurrentPassword("CurrentPassword");
    updateRequest.setNewPassword("NewPassword");

    Set<ConstraintViolation<UpdateRequest>> actual = validator.validate(updateRequest);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message").containsOnly("メールアドレスを入力してください。");
  }

  @Test
  void Email形式でない値を設定した時入力チェックに異常が発生すること() {
    UpdateRequest updateRequest = new UpdateRequest();
    updateRequest.setUsername("DummyUser");
    updateRequest.setEmail("abc");
    updateRequest.setDisplayName("DummyUser");
    updateRequest.setCurrentPassword("CurrentPassword");
    updateRequest.setNewPassword("NewPassword");

    Set<ConstraintViolation<UpdateRequest>> actual = validator.validate(updateRequest);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("有効なメールアドレスを入力してください。");
  }

  @Test
  void 表示名がnullの時入力チェックに異常が発生すること() {
    UpdateRequest updateRequest = new UpdateRequest();
    updateRequest.setUsername("DummyUser");
    updateRequest.setEmail("dummy@example.com");
    updateRequest.setCurrentPassword("CurrentPassword");
    updateRequest.setNewPassword("NewPassword");

    Set<ConstraintViolation<UpdateRequest>> actual = validator.validate(updateRequest);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message").containsOnly("表示名を入力してください。");
  }

  @Test
  void 表示名が空の時入力チェックに異常が発生すること() {
    UpdateRequest updateRequest = new UpdateRequest();
    updateRequest.setUsername("DummyUser");
    updateRequest.setEmail("dummy@example.com");
    updateRequest.setDisplayName("");
    updateRequest.setCurrentPassword("CurrentPassword");
    updateRequest.setNewPassword("NewPassword");

    Set<ConstraintViolation<UpdateRequest>> actual = validator.validate(updateRequest);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message").containsOnly("表示名を入力してください。");
  }

  @Test
  void 新規のパスワードが5文字の時入力チェックに異常が発生すること() {
    UpdateRequest updateRequest = new UpdateRequest();
    updateRequest.setUsername("DummyUser");
    updateRequest.setEmail("dummy@example.com");
    updateRequest.setDisplayName("DummyUser");
    updateRequest.setCurrentPassword("CurrentPassword");
    updateRequest.setNewPassword("12345");

    Set<ConstraintViolation<UpdateRequest>> actual = validator.validate(updateRequest);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("パスワードは6文字以上で入力してください。");
  }

  @Test
  void 新規のパスワードが6文字の時入力チェックに異常が発生しないこと() {
    UpdateRequest updateRequest = new UpdateRequest();
    updateRequest.setUsername("DummyUser");
    updateRequest.setEmail("dummy@example.com");
    updateRequest.setDisplayName("DummyUser");
    updateRequest.setCurrentPassword("CurrentPassword");
    updateRequest.setNewPassword("123456");

    Set<ConstraintViolation<UpdateRequest>> actual = validator.validate(updateRequest);

    assertThat(actual.size()).isEqualTo(0);
  }

}
