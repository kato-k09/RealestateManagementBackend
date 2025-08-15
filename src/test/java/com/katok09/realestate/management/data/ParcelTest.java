package com.katok09.realestate.management.data;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class ParcelTest {

  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void setterとgetterが正常に動作すること() {
    Parcel actual = new Parcel();
    actual.setId(999);
    actual.setProjectId(999);
    actual.setUserId(999);
    actual.setParcelPrice(10000000L);
    actual.setParcelAddress("東京都");
    actual.setParcelCategory("宅地");
    actual.setParcelSize(123.45);
    actual.setParcelRemark("特に無し");
    actual.setDeleted(false);

    assertThat(actual.getId()).isEqualTo(999);
    assertThat(actual.getProjectId()).isEqualTo(999);
    assertThat(actual.getUserId()).isEqualTo(999);
    assertThat(actual.getParcelPrice()).isEqualTo(10000000);
    assertThat(actual.getParcelAddress()).isEqualTo("東京都");
    assertThat(actual.getParcelCategory()).isEqualTo("宅地");
    assertThat(actual.getParcelSize()).isEqualTo(123.45);
    assertThat(actual.getParcelRemark()).isEqualTo("特に無し");
    assertThat(actual.isDeleted()).isFalse();
  }

  @Test
  void 土地価格が0円の時入力チェックに異常が発生しないこと() {
    Parcel parcel = new Parcel();
    parcel.setParcelPrice(0L);

    Set<ConstraintViolation<Parcel>> actual = validator.validate(parcel);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 土地価格がマイナス1円の時入力チェックに異常が発生すること() {
    Parcel parcel = new Parcel();
    parcel.setParcelPrice(-1L);

    Set<ConstraintViolation<Parcel>> actual = validator.validate(parcel);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message").containsOnly("土地価格は0円以上で入力してください。");
  }

  @Test
  void 土地価格が1000兆円の時入力チェックに異常が発生しないこと() {
    Parcel parcel = new Parcel();
    parcel.setParcelPrice(1000L * 1000L * 1000L * 1000L * 1000L);

    Set<ConstraintViolation<Parcel>> actual = validator.validate(parcel);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 土地価格が1000兆1円の時入力チェックに異常が発生すること() {
    Parcel parcel = new Parcel();
    parcel.setParcelPrice(1000L * 1000L * 1000L * 1000L * 1000L + 1L);

    Set<ConstraintViolation<Parcel>> actual = validator.validate(parcel);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("土地価格は1,000兆円以下で入力してください。");
  }

  @Test
  void 土地住所が100字の時入力チェックに異常が発生しないこと() {
    Parcel parcel = new Parcel();
    parcel.setParcelAddress("a".repeat(100));

    Set<ConstraintViolation<Parcel>> actual = validator.validate(parcel);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 土地住所が101字の時入力チェックに異常が発生すること() {
    Parcel parcel = new Parcel();
    parcel.setParcelAddress("a".repeat(101));

    Set<ConstraintViolation<Parcel>> actual = validator.validate(parcel);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("土地住所は100文字以下で入力してください。");
  }

  @Test
  void 土地住所がnullの時入力チェックに異常が発生しないこと() {
    Parcel parcel = new Parcel();
    parcel.setParcelAddress(null);

    Set<ConstraintViolation<Parcel>> actual = validator.validate(parcel);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 土地種別が50字の時入力チェックに異常が発生しないこと() {
    Parcel parcel = new Parcel();
    parcel.setParcelCategory("a".repeat(50));

    Set<ConstraintViolation<Parcel>> actual = validator.validate(parcel);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 土地種別が51字の時入力チェックに異常が発生すること() {
    Parcel parcel = new Parcel();
    parcel.setParcelCategory("a".repeat(51));

    Set<ConstraintViolation<Parcel>> actual = validator.validate(parcel);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("土地種別は50文字以下で入力してください。");
  }

  @Test
  void 土地種別がnullの時入力チェックに異常が発生しないこと() {
    Parcel parcel = new Parcel();
    parcel.setParcelCategory(null);

    Set<ConstraintViolation<Parcel>> actual = validator.validate(parcel);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 土地面積が0の時入力チェックに異常が発生しないこと() {
    Parcel parcel = new Parcel();
    parcel.setParcelSize(0L);

    Set<ConstraintViolation<Parcel>> actual = validator.validate(parcel);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 土地面積がマイナス1の時入力チェックに異常が発生すること() {
    Parcel parcel = new Parcel();
    parcel.setParcelSize(-1L);

    Set<ConstraintViolation<Parcel>> actual = validator.validate(parcel);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("土地面積は0㎡以上で入力してください。");
  }

  @Test
  void 土地面積が10億の時入力チェックに異常が発生しないこと() {
    Parcel parcel = new Parcel();
    parcel.setParcelSize(1000L * 1000L * 1000L);

    Set<ConstraintViolation<Parcel>> actual = validator.validate(parcel);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 土地面積が10億1の時入力チェックに異常が発生すること() {
    Parcel parcel = new Parcel();
    parcel.setParcelSize(1000L * 1000L * 1000L + 1L);

    Set<ConstraintViolation<Parcel>> actual = validator.validate(parcel);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("土地面積は10億㎡以下で入力してください。");
  }

  @Test
  void 土地備考が100字の時入力チェックに異常が発生しないこと() {
    Parcel parcel = new Parcel();
    parcel.setParcelRemark("a".repeat(100));

    Set<ConstraintViolation<Parcel>> actual = validator.validate(parcel);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 土地備考が101字の時入力チェックに異常が発生しないこと() {
    Parcel parcel = new Parcel();
    parcel.setParcelRemark("a".repeat(101));

    Set<ConstraintViolation<Parcel>> actual = validator.validate(parcel);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("土地備考は100文字以下で入力してください。");
  }

  @Test
  void 土地備考がnullの時入力チェックに異常が発生しないこと() {
    Parcel parcel = new Parcel();
    parcel.setParcelRemark(null);

    Set<ConstraintViolation<Parcel>> actual = validator.validate(parcel);

    assertThat(actual.size()).isEqualTo(0);
  }

}
