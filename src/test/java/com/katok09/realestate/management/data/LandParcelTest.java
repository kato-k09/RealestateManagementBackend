package com.katok09.realestate.management.data;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class LandParcelTest {

  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void 土地価格が0円の時入力チェックに異常が発生しないこと() {
    LandParcel landParcel = new LandParcel();
    landParcel.setLandParcelPrice(0L);

    Set<ConstraintViolation<LandParcel>> actual = validator.validate(landParcel);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 土地価格がマイナス1円の時入力チェックに異常が発生すること() {
    LandParcel landParcel = new LandParcel();
    landParcel.setLandParcelPrice(-1L);

    Set<ConstraintViolation<LandParcel>> actual = validator.validate(landParcel);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message").containsOnly("土地価格は0円以上で入力してください。");
  }

  @Test
  void 土地価格が1000兆円の時入力チェックに異常が発生しないこと() {
    LandParcel landParcel = new LandParcel();
    landParcel.setLandParcelPrice(1000L * 1000L * 1000L * 1000L * 1000L);

    Set<ConstraintViolation<LandParcel>> actual = validator.validate(landParcel);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 土地価格が1000兆1円の時入力チェックに異常が発生すること() {
    LandParcel landParcel = new LandParcel();
    landParcel.setLandParcelPrice(1000L * 1000L * 1000L * 1000L * 1000L + 1L);

    Set<ConstraintViolation<LandParcel>> actual = validator.validate(landParcel);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("土地価格は1,000兆円以下で入力してください。");
  }

  @Test
  void 土地住所が100字の時入力チェックに異常が発生しないこと() {
    LandParcel landParcel = new LandParcel();
    landParcel.setLandParcelAddress("a".repeat(100));

    Set<ConstraintViolation<LandParcel>> actual = validator.validate(landParcel);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 土地住所が101字の時入力チェックに異常が発生すること() {
    LandParcel landParcel = new LandParcel();
    landParcel.setLandParcelAddress("a".repeat(101));

    Set<ConstraintViolation<LandParcel>> actual = validator.validate(landParcel);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("土地住所は100文字以下で入力してください。");
  }

  @Test
  void 土地住所がnullの時入力チェックに異常が発生しないこと() {
    LandParcel landParcel = new LandParcel();
    landParcel.setLandParcelAddress(null);

    Set<ConstraintViolation<LandParcel>> actual = validator.validate(landParcel);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 土地種別が50字の時入力チェックに異常が発生しないこと() {
    LandParcel landParcel = new LandParcel();
    landParcel.setLandParcelCategory("a".repeat(50));

    Set<ConstraintViolation<LandParcel>> actual = validator.validate(landParcel);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 土地種別が51字の時入力チェックに異常が発生すること() {
    LandParcel landParcel = new LandParcel();
    landParcel.setLandParcelCategory("a".repeat(51));

    Set<ConstraintViolation<LandParcel>> actual = validator.validate(landParcel);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("土地種別は50文字以下で入力してください。");
  }

  @Test
  void 土地種別がnullの時入力チェックに異常が発生しないこと() {
    LandParcel landParcel = new LandParcel();
    landParcel.setLandParcelCategory(null);

    Set<ConstraintViolation<LandParcel>> actual = validator.validate(landParcel);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 土地面積が0の時入力チェックに異常が発生しないこと() {
    LandParcel landParcel = new LandParcel();
    landParcel.setLandParcelSize(0L);

    Set<ConstraintViolation<LandParcel>> actual = validator.validate(landParcel);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 土地面積がマイナス1の時入力チェックに異常が発生すること() {
    LandParcel landParcel = new LandParcel();
    landParcel.setLandParcelSize(-1L);

    Set<ConstraintViolation<LandParcel>> actual = validator.validate(landParcel);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("土地面積は0㎡以上で入力してください。");
  }

  @Test
  void 土地面積が10億の時入力チェックに異常が発生しないこと() {
    LandParcel landParcel = new LandParcel();
    landParcel.setLandParcelSize(1000L * 1000L * 1000L);

    Set<ConstraintViolation<LandParcel>> actual = validator.validate(landParcel);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 土地面積が10億1の時入力チェックに異常が発生すること() {
    LandParcel landParcel = new LandParcel();
    landParcel.setLandParcelSize(1000L * 1000L * 1000L + 1L);

    Set<ConstraintViolation<LandParcel>> actual = validator.validate(landParcel);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("土地面積は10億㎡以下で入力してください。");
  }

  @Test
  void 土地備考が100字の時入力チェックに異常が発生しないこと() {
    LandParcel landParcel = new LandParcel();
    landParcel.setLandParcelRemark("a".repeat(100));

    Set<ConstraintViolation<LandParcel>> actual = validator.validate(landParcel);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 土地備考が101字の時入力チェックに異常が発生すること() {
    LandParcel landParcel = new LandParcel();
    landParcel.setLandParcelRemark("a".repeat(101));

    Set<ConstraintViolation<LandParcel>> actual = validator.validate(landParcel);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("土地備考は100文字以下で入力してください。");
  }

  @Test
  void 土地備考がnullの時入力チェックに異常が発生しないこと() {
    LandParcel landParcel = new LandParcel();
    landParcel.setLandParcelRemark(null);

    Set<ConstraintViolation<LandParcel>> actual = validator.validate(landParcel);

    assertThat(actual.size()).isEqualTo(0);
  }

}
