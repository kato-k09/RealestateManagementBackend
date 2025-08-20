package com.katok09.realestate.management.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "不動産収支情報")
@Getter
@Setter
public class IncomeAndExpenses {

  @Schema(description = "収支ID DB登録時に自動採番されます。", example = "99")
  private int id;
  @Schema(description = "プロジェクトID プロジェクトごとに紐づけをします。", example = "99")
  private int projectId;
  @Schema(description = "ユーザーID", example = "99")
  private int userId;
  @Schema(description = "月収入", example = "100000")
  @Min(value = 0, message = "月収入は0円以上で入力してください。")
  @Max(value = 1000000000, message = "月収入は10億円以下で入力してください。")
  private int rent;
  @Schema(description = "管理費", example = "10000")
  @Min(value = 0, message = "管理費は0円以上で入力してください。")
  @Max(value = 1000000000, message = "管理費は10億円以下で入力してください。")
  private int maintenanceCost;
  @Schema(description = "修繕積立金", example = "5000")
  @Min(value = 0, message = "修繕積立金は0円以上で入力してください。")
  @Max(value = 1000000000, message = "修繕積立金は10億円以下で入力してください。")
  private int repairFund;
  @Schema(description = "管理委託費", example = "3000")
  @Min(value = 0, message = "管理委託費は0円以上で入力してください。")
  @Max(value = 1000000000, message = "管理委託費は10億円以下で入力してください。")
  private int managementFee;
  @Schema(description = "ローン元金月返済", example = "20000")
  @Min(value = 0, message = "ローン元金月返済は0円以上で入力してください。")
  @Max(value = 1000000000, message = "ローン元金は10億円以下で入力してください。")
  private int principal;
  @Schema(description = "ローン利息月返済", example = "5000")
  @Min(value = 0, message = "ローン利息月返済は0円以上で入力してください。")
  @Max(value = 1000000000, message = "ローン利息月返済は10億円以下で入力してください。")
  private int interest;
  @Schema(description = "税金", example = "3000")
  @Min(value = 0, message = "税金は0円以上で入力してください。")
  @Max(value = 1000000000, message = "税金は10億円以下で入力してください。")
  private int tax;
  @Schema(description = "水道料金", example = "2000")
  @Min(value = 0, message = "水道料金は0円以上で入力してください。")
  @Max(value = 1000000000, message = "水道料金は10億円以下で入力してください。")
  private int waterBill;
  @Schema(description = "電気料金", example = "1500")
  @Min(value = 0, message = "電気料金は0円以上で入力してください。")
  @Max(value = 1000000000, message = "電気料金は10億円以下で入力してください。")
  private int electricBill;
  @Schema(description = "ガス料金", example = "1000")
  @Min(value = 0, message = "ガス料金は0円以上で入力してください。")
  @Max(value = 1000000000, message = "ガス料金は10億円以下で入力してください。")
  private int gasBill;
  @Schema(description = "火災保険料", example = "2500")
  @Min(value = 0, message = "火災保険料は0円以上で入力してください。")
  @Max(value = 1000000000, message = "火災保険料は10億円以下で入力してください。")
  private int fireInsurance;
  @Schema(description = "備考", example = "特になし")
  @Size(max = 100, message = "備考は100文字以下で入力してください。")
  private String other;
  @Schema(description = "削除フラグ", example = "false")
  private boolean isDeleted;

}
