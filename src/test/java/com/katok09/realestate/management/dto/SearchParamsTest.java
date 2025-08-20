package com.katok09.realestate.management.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class SearchParamsTest {

  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void プロジェクト名が100字の時入力チェックに異常が発生しないこと() {
    SearchParams searchParams = new SearchParams();
    searchParams.setSearchProjectName("a".repeat(100));

    Set<ConstraintViolation<SearchParams>> actual = validator.validate(searchParams);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void プロジェクト名が101字の時入力チェックに異常が発生すること() {
    SearchParams searchParams = new SearchParams();
    searchParams.setSearchProjectName("a".repeat(101));

    Set<ConstraintViolation<SearchParams>> actual = validator.validate(searchParams);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("プロジェクト名は100字以内で入力してください。");
  }

  @Test
  void プロジェクト名がnullの時入力チェックに異常が発生しないこと() {
    SearchParams searchParams = new SearchParams();
    searchParams.setSearchProjectName(null);

    Set<ConstraintViolation<SearchParams>> actual = validator.validate(searchParams);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 住所が100字の時入力チェックに異常が発生しないこと() {
    SearchParams searchParams = new SearchParams();
    searchParams.setSearchParcelAddress("a".repeat(100));

    Set<ConstraintViolation<SearchParams>> actual = validator.validate(searchParams);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 住所が101字の時入力チェックに異常が発生すること() {
    SearchParams searchParams = new SearchParams();
    searchParams.setSearchParcelAddress("a".repeat(101));

    Set<ConstraintViolation<SearchParams>> actual = validator.validate(searchParams);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("住所は100字以内で入力してください。");
  }

  @Test
  void 住所がnullの時入力チェックに異常が発生しないこと() {
    SearchParams searchParams = new SearchParams();
    searchParams.setSearchParcelAddress(null);

    Set<ConstraintViolation<SearchParams>> actual = validator.validate(searchParams);

    assertThat(actual.size()).isEqualTo(0);
  }

  @ParameterizedTest
  @ValueSource(strings = {"マンション", "アパート", "戸建て", "店舗", "事務所", "その他", ""})
  void 有効な建物種別が設定された時入力チェックに異常が発生しないこと(String searchBuildingType) {
    SearchParams searchParams = new SearchParams();
    searchParams.setSearchBuildingType(searchBuildingType);

    Set<ConstraintViolation<SearchParams>> actual = validator.validate(searchParams);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 無効な建物種別が設定された時入力チェックに異常が発生すること() {
    SearchParams searchParams = new SearchParams();
    searchParams.setSearchBuildingType("無効な建物種別");

    Set<ConstraintViolation<SearchParams>> actual = validator.validate(searchParams);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("建物種別は指定された選択肢から選んでください。");
  }

  @Test
  void 建物種別がnullの時入力チェックに異常が発生しないこと() {
    SearchParams searchParams = new SearchParams();
    searchParams.setSearchBuildingType(null);

    Set<ConstraintViolation<SearchParams>> actual = validator.validate(searchParams);

    assertThat(actual.size()).isEqualTo(0);
  }

  @ParameterizedTest
  @ValueSource(strings = {"鉄筋コンクリート造", "鉄骨造", "木造", "軽量鉄骨造", "その他", ""})
  void 有効な建物構造が設定された時入力チェックに異常が発生しないこと(String searchBuildingType) {
    SearchParams searchParams = new SearchParams();
    searchParams.setSearchBuildingStructure(searchBuildingType);

    Set<ConstraintViolation<SearchParams>> actual = validator.validate(searchParams);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 無効な建物構造が設定された時入力チェックに異常が発生すること() {
    SearchParams searchParams = new SearchParams();
    searchParams.setSearchBuildingStructure("無効な建物構造");

    Set<ConstraintViolation<SearchParams>> actual = validator.validate(searchParams);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("建物構造は指定された選択肢から選んでください。");
  }

  @Test
  void 建物構造がnullの時入力チェックに異常が発生しないこと() {
    SearchParams searchParams = new SearchParams();
    searchParams.setSearchBuildingStructure(null);

    Set<ConstraintViolation<SearchParams>> actual = validator.validate(searchParams);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 融資の有無がnullの時入力チェックに異常が発生しないこと() {
    SearchParams searchParams = new SearchParams();
    searchParams.setSearchFinancing(null);

    Set<ConstraintViolation<SearchParams>> actual = validator.validate(searchParams);

    assertThat(actual.size()).isEqualTo(0);
  }

}
