package com.katok09.realestate.management.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class StatusRequestTest {

  private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @ParameterizedTest
  @ValueSource(strings = {"USER", "ADMIN", "GUEST"})
  void 有効なロールが設定された時入力チェックに異常が発生しないこと(String role) {
    StatusRequest statusRequest = new StatusRequest();
    statusRequest.setRole(role);

    Set<ConstraintViolation<StatusRequest>> actual = validator.validate(statusRequest);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 無効なロールが設定された時入力チェックに異常が発生すること() {
    StatusRequest statusRequest = new StatusRequest();
    statusRequest.setRole("UNKNOWN");

    Set<ConstraintViolation<StatusRequest>> actual = validator.validate(statusRequest);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("ADMINまたはUSERまたはGUESTを選択してください。");
  }

  @Test
  void ロールがnullの時入力チェックに異常が発生すること() {
    StatusRequest statusRequest = new StatusRequest();

    Set<ConstraintViolation<StatusRequest>> actual = validator.validate(statusRequest);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("ロールは必須です。");
  }

  @Test
  void ロールが空の時入力チェックに異常が発生すること() {
    StatusRequest statusRequest = new StatusRequest();
    statusRequest.setRole("");

    Set<ConstraintViolation<StatusRequest>> actual = validator.validate(statusRequest);

    assertThat(actual.size()).isEqualTo(2);
    assertThat(actual).extracting("message")
        .containsOnly("ロールは必須です。", "ADMINまたはUSERまたはGUESTを選択してください。");
  }

}
