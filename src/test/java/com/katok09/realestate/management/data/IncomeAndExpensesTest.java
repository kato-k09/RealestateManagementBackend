package com.katok09.realestate.management.data;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class IncomeAndExpensesTest {

  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void setterとgetterが正常に動作すること() {
    IncomeAndExpenses actual = new IncomeAndExpenses();
    actual.setId(999);
    actual.setProjectId(999);
    actual.setUserId(999);
    actual.setRent(100000);
    actual.setMaintenanceCost(1000);
    actual.setRepairFund(1000);
    actual.setManagementFee(1000);
    actual.setPrincipal(1000);
    actual.setInterest(1000);
    actual.setTax(1000);
    actual.setWaterBill(1000);
    actual.setElectricBill(1000);
    actual.setGasBill(1000);
    actual.setFireInsurance(1000);
    actual.setOther("特に無し");
    actual.setDeleted(false);

    assertThat(actual.getId()).isEqualTo(999);
    assertThat(actual.getProjectId()).isEqualTo(999);
    assertThat(actual.getUserId()).isEqualTo(999);
    assertThat(actual.getRent()).isEqualTo(100000);
    assertThat(actual.getMaintenanceCost()).isEqualTo(1000);
    assertThat(actual.getRepairFund()).isEqualTo(1000);
    assertThat(actual.getManagementFee()).isEqualTo(1000);
    assertThat(actual.getPrincipal()).isEqualTo(1000);
    assertThat(actual.getTax()).isEqualTo(1000);
    assertThat(actual.getWaterBill()).isEqualTo(1000);
    assertThat(actual.getElectricBill()).isEqualTo(1000);
    assertThat(actual.getGasBill()).isEqualTo(1000);
    assertThat(actual.getFireInsurance()).isEqualTo(1000);
    assertThat(actual.getOther()).isEqualTo("特に無し");
    assertThat(actual.isDeleted()).isFalse();
  }

  @Test
  void 月収入が0円の時入力チェックに異常が発生しないこと() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setRent(0);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 月収入がマイナス1円の時入力チェックに異常が発生すること() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setRent(-1);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message").containsOnly("月収入は0円以上で入力してください。");
  }

  @Test
  void 月収入が10億円の時入力チェックに異常が発生しないこと() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setRent(1000 * 1000 * 1000);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 月収入が10億1円の時入力チェックに異常が発生すること() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setRent(1000 * 1000 * 1000 + 1);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message").containsOnly("月収入は10億円以下で入力してください。");
  }

  @Test
  void 管理費が0円の時入力チェックに異常が発生しないこと() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setMaintenanceCost(0);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 管理費がマイナス1円の時入力チェックに異常が発生すること() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setMaintenanceCost(-1);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message").containsOnly("管理費は0円以上で入力してください。");
  }

  @Test
  void 管理費が10億円の時入力チェックに異常が発生しないこと() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setMaintenanceCost(1000 * 1000 * 1000);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 管理費が10億1円の時入力チェックに異常が発生すること() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setMaintenanceCost(1000 * 1000 * 1000 + 1);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message").containsOnly("管理費は10億円以下で入力してください。");
  }

  @Test
  void 修繕積立金が0円の時入力チェックに異常が発生しないこと() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setRepairFund(0);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 修繕積立金がマイナス1円の時入力チェックに異常が発生すること() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setRepairFund(-1);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message").containsOnly("修繕積立金は0円以上で入力してください。");
  }

  @Test
  void 修繕積立金が10億円の時入力チェックに異常が発生しないこと() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setRepairFund(1000 * 1000 * 1000);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 修繕積立金が10億1円の時入力チェックに異常が発生すること() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setRepairFund(1000 * 1000 * 1000 + 1);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("修繕積立金は10億円以下で入力してください。");
  }

  @Test
  void 管理委託費が0円の時入力チェックに異常が発生しないこと() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setManagementFee(0);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 管理委託費がマイナス1円の時入力チェックに異常が発生すること() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setManagementFee(-1);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message").containsOnly("管理委託費は0円以上で入力してください。");
  }

  @Test
  void 管理委託費が10億円の時入力チェックに異常が発生しないこと() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setManagementFee(1000 * 1000 * 1000);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 管理委託費が10億1円の時入力チェックに異常が発生すること() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setManagementFee(1000 * 1000 * 1000 + 1);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("管理委託費は10億円以下で入力してください。");
  }

  @Test
  void ローン元金月返済が0円の時入力チェックに異常が発生しないこと() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setPrincipal(0);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void ローン元金月返済がマイナス1円の時入力チェックに異常が発生すること() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setPrincipal(-1);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("ローン元金月返済は0円以上で入力してください。");
  }

  @Test
  void ローン元金月返済が10億円の時入力チェックに異常が発生しないこと() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setPrincipal(1000 * 1000 * 1000);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void ローン元金月返済が10億1円の時入力チェックに異常が発生すること() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setPrincipal(1000 * 1000 * 1000 + 1);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("ローン元金は10億円以下で入力してください。");
  }

  @Test
  void ローン利息月返済が0円の時入力チェックに異常が発生しないこと() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setInterest(0);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void ローン利息月返済がマイナス1円の時入力チェックに異常が発生すること() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setInterest(-1);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("ローン利息月返済は0円以上で入力してください。");
  }

  @Test
  void ローン利息月返済が10億円の時入力チェックに異常が発生しないこと() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setInterest(1000 * 1000 * 1000);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void ローン利息月返済が10億1円の時入力チェックに異常が発生すること() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setInterest(1000 * 1000 * 1000 + 1);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("ローン利息月返済は10億円以下で入力してください。");
  }

  @Test
  void 税金が0円の時入力チェックに異常が発生しないこと() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setTax(0);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 税金がマイナス1円の時入力チェックに異常が発生すること() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setTax(-1);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message").containsOnly("税金は0円以上で入力してください。");
  }

  @Test
  void 税金が10億円の時入力チェックに異常が発生しないこと() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setTax(1000 * 1000 * 1000);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 税金が10億1円の時入力チェックに異常が発生すること() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setTax(1000 * 1000 * 1000 + 1);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message").containsOnly("税金は10億円以下で入力してください。");
  }

  @Test
  void 水道料金が0円の時入力チェックに異常が発生しないこと() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setWaterBill(0);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 水道料金がマイナス1円の時入力チェックに異常が発生すること() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setWaterBill(-1);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message").containsOnly("水道料金は0円以上で入力してください。");
  }

  @Test
  void 水道料金が10億円の時入力チェックに異常が発生しないこと() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setWaterBill(1000 * 1000 * 1000);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 水道料金が10億1円の時入力チェックに異常が発生すること() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setWaterBill(1000 * 1000 * 1000 + 1);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("水道料金は10億円以下で入力してください。");
  }

  @Test
  void 電気料金が0円の時入力チェックに異常が発生しないこと() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setElectricBill(0);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 電気料金がマイナス1円の時入力チェックに異常が発生すること() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setElectricBill(-1);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message").containsOnly("電気料金は0円以上で入力してください。");
  }

  @Test
  void 電気料金が10億円の時入力チェックに異常が発生しないこと() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setElectricBill(1000 * 1000 * 1000);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 電気料金が10億1円の時入力チェックに異常が発生すること() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setElectricBill(1000 * 1000 * 1000 + 1);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("電気料金は10億円以下で入力してください。");
  }

  @Test
  void ガス料金が0円の時入力チェックに異常が発生しないこと() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setGasBill(0);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void ガス料金がマイナス1円の時入力チェックに異常が発生すること() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setGasBill(-1);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message").containsOnly("ガス料金は0円以上で入力してください。");
  }

  @Test
  void ガス料金が10億円の時入力チェックに異常が発生しないこと() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setGasBill(1000 * 1000 * 1000);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void ガス料金が10億1円の時入力チェックに異常が発生すること() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setGasBill(1000 * 1000 * 1000 + 1);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("ガス料金は10億円以下で入力してください。");
  }

  @Test
  void 火災保険料が0円の時入力チェックに異常が発生しないこと() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setFireInsurance(0);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 火災保険料がマイナス1円の時入力チェックに異常が発生すること() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setFireInsurance(-1);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message").containsOnly("火災保険料は0円以上で入力してください。");
  }

  @Test
  void 火災保険料が10億円の時入力チェックに異常が発生しないこと() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setFireInsurance(1000 * 1000 * 1000);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 火災保険料が10億1円の時入力チェックに異常が発生すること() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setFireInsurance(1000 * 1000 * 1000 + 1);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("火災保険料は10億円以下で入力してください。");
  }

  @Test
  void 備考が100文字の時入力チェックに異常が発生しないこと() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setOther("a".repeat(100));

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void 備考が101文字の時入力チェックに異常が発生すること() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setOther("a".repeat(101));

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message").containsOnly("備考は100文字以下で入力してください。");
  }

  @Test
  void 備考がnullの時入力チェックに異常が発生しないこと() {
    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setOther(null);

    Set<ConstraintViolation<IncomeAndExpenses>> actual = validator.validate(incomeAndExpenses);
    assertThat(actual.size()).isEqualTo(0);
  }

}
