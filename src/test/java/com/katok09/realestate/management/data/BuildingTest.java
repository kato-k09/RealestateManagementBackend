package com.katok09.realestate.management.data;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class BuildingTest {

  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void setterとgetterが正常に動作すること() {
    Building actual = new Building();
    actual.setId(999);
    actual.setProjectId(999);
    actual.setUserId(999);
    actual.setBuildingPrice(10000000L);
    actual.setBuildingType("アパート");
    actual.setBuildingStructure("木造");
    actual.setBuildingSize(123.45);
    actual.setBuildingDate(LocalDate.of(2025, 1, 1));
    actual.setBuildingRemark("特に無し");
    actual.setDeleted(false);

    assertThat(actual.getId()).isEqualTo(999);
    assertThat(actual.getProjectId()).isEqualTo(999);
    assertThat(actual.getUserId()).isEqualTo(999);
    assertThat(actual.getBuildingPrice()).isEqualTo(10000000L);
    assertThat(actual.getBuildingType()).isEqualTo("アパート");
    assertThat(actual.getBuildingStructure()).isEqualTo("木造");
    assertThat(actual.getBuildingSize()).isEqualTo(123.45);
    assertThat(actual.getBuildingDate()).isEqualTo(LocalDate.of(2025, 1, 1));
    assertThat(actual.getBuildingRemark()).isEqualTo("特に無し");
    assertThat(actual.isDeleted()).isFalse();
  }

  @Test
  void 建物価格が0円の時入力チェックに異常が発生しないこと() {
    Building building = new Building();
    building.setBuildingPrice(0L);

    Set<ConstraintViolation<Building>> actual = validator.validate(building);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 建物価格がマイナス1円の時入力チェックに異常が発生すること() {
    Building building = new Building();
    building.setBuildingPrice(-1L);

    Set<ConstraintViolation<Building>> actual = validator.validate(building);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message").containsOnly("建物価格は0円以上で入力してください。");
  }

  @Test
  void 建物価格が1000兆円の時入力チェックに異常が発生しないこと() {
    Building building = new Building();
    building.setBuildingPrice(1000L * 1000L * 1000L * 1000L * 1000L);

    Set<ConstraintViolation<Building>> actual = validator.validate(building);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 建物価格が1000兆1円の時入力チェックに異常が発生すること() {
    Building building = new Building();
    building.setBuildingPrice(1000L * 1000L * 1000L * 1000L * 1000L + 1);

    Set<ConstraintViolation<Building>> actual = validator.validate(building);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("建物価格は1,000兆円以下で入力してください。");
  }

  @ParameterizedTest
  @ValueSource(strings = {"マンション", "アパート", "戸建て", "店舗", "事務所", "その他", ""})
  void 有効な建物種別が設定された時入力チェックに異常が発生しないこと(String buildingType) {
    Building building = new Building();
    building.setBuildingType(buildingType);

    Set<ConstraintViolation<Building>> actual = validator.validate(building);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 建物種別がnullの時入力チェックに異常が発生しないこと() {
    Building building = new Building();
    building.setBuildingType(null);

    Set<ConstraintViolation<Building>> actual = validator.validate(building);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 無効な建物種別が設定された時入力チェックに異常が発生すること() {
    Building building = new Building();
    building.setBuildingType("無効な建物種別");

    Set<ConstraintViolation<Building>> actual = validator.validate(building);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("建物種別は指定された選択肢から選んでください。");
  }

  @ParameterizedTest
  @ValueSource(strings = {"鉄筋コンクリート造", "鉄骨造", "木造", "軽量鉄骨造", "その他", ""})
  void 有効な建物構造が設定された時入力チェックに異常が発生しないこと(String buildingStructure) {
    Building building = new Building();
    building.setBuildingStructure(buildingStructure);

    Set<ConstraintViolation<Building>> actual = validator.validate(building);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 建物構造がnullの時入力チェックに異常が発生しないこと() {
    Building building = new Building();
    building.setBuildingStructure(null);

    Set<ConstraintViolation<Building>> actual = validator.validate(building);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 無効な建物構造が設定された時に入力チェックに異常が発生すること() {
    Building building = new Building();
    building.setBuildingStructure("無効な建物構造");

    Set<ConstraintViolation<Building>> actual = validator.validate(building);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("建物構造は指定された選択肢から選んでください。");
  }

  @Test
  void 建物面積が0の時入力チェックに異常が発生しないこと() {
    Building building = new Building();
    building.setBuildingSize(0L);

    Set<ConstraintViolation<Building>> actual = validator.validate(building);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 建物面積がマイナス1の時入力チェックに異常が発生すること() {
    Building building = new Building();
    building.setBuildingSize(-1L);

    Set<ConstraintViolation<Building>> actual = validator.validate(building);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("建物面積は0㎡以上で入力してください。");
  }

  @Test
  void 建物面積が10億の時入力チェックに異常が発生しないこと() {
    Building building = new Building();
    building.setBuildingSize(1000L * 1000L * 1000L);

    Set<ConstraintViolation<Building>> actual = validator.validate(building);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 建物面積が10億1の時入力チェックに異常が発生すること() {
    Building building = new Building();
    building.setBuildingSize(1000L * 1000L * 1000L + 1L);

    Set<ConstraintViolation<Building>> actual = validator.validate(building);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("建物面積は10億㎡以下で入力してください。");
  }

  @Test
  void 建物築年が1000年1月1日の時入力チェックに異常が発生しないこと() {
    Building building = new Building();
    building.setBuildingDate(LocalDate.of(1000, 1, 1));

    Set<ConstraintViolation<Building>> actual = validator.validate(building);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 建物築年が999年12月31日の時入力チェックに異常が発生すること() {
    Building building = new Building();
    building.setBuildingDate(LocalDate.of(999, 12, 31));

    Set<ConstraintViolation<Building>> actual = validator.validate(building);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("築年月日は1000年から2999年までの範囲で入力してください。");
  }

  @Test
  void 建物築年が2999年12月31日の時入力チェックに異常が発生しないこと() {
    Building building = new Building();
    building.setBuildingDate(LocalDate.of(2999, 12, 31));

    Set<ConstraintViolation<Building>> actual = validator.validate(building);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 建物築年が3000年1月1日の時入力チェックに異常が発生すること() {
    Building building = new Building();
    building.setBuildingDate(LocalDate.of(3000, 1, 1));

    Set<ConstraintViolation<Building>> actual = validator.validate(building);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("築年月日は1000年から2999年までの範囲で入力してください。");
  }

  @Test
  void 建物築年がnullの時入力チェックに異常が発生しないこと() {
    Building building = new Building();
    building.setBuildingDate(null);

    Set<ConstraintViolation<Building>> actual = validator.validate(building);

    assertThat(actual.size()).isEqualTo(0);
  }


  @Test
  void 建物備考が100文字の時入力チェックに異常が発生しないこと() {
    Building building = new Building();
    building.setBuildingRemark("a".repeat(100));

    Set<ConstraintViolation<Building>> actual = validator.validate(building);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 建物備考が101文字の時入力チェックに異常が発生すること() {
    Building building = new Building();
    building.setBuildingRemark("a".repeat(101));

    Set<ConstraintViolation<Building>> actual = validator.validate(building);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("建物備考は100文字以下で入力してください。");
  }

  @Test
  void 建物備考がnullの時入力チェックに異常が発生しないこと() {
    Building building = new Building();
    building.setBuildingRemark(null);

    Set<ConstraintViolation<Building>> actual = validator.validate(building);

    assertThat(actual.size()).isEqualTo(0);
  }

}
