package com.katok09.realestate.management.data;

import io.swagger.v3.oas.annotations.media.Schema;
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
  @Schema(description = "月収入", example = "100000")
  private int rent;
  @Schema(description = "管理費", example = "10000")
  private int maintenanceCost;
  @Schema(description = "修繕積立金", example = "5000")
  private int repairFund;
  @Schema(description = "管理委託費", example = "3000")
  private int managementFee;
  @Schema(description = "ローン元金", example = "20000")
  private int principal;
  @Schema(description = "ローン利息", example = "5000")
  private int interest;
  @Schema(description = "税金", example = "3000")
  private int tax;
  @Schema(description = "水道料金", example = "2000")
  private int waterBill;
  @Schema(description = "電気料金", example = "1500")
  private int electricBill;
  @Schema(description = "ガス料金", example = "1000")
  private int gasBill;
  @Schema(description = "火災保険料", example = "2500")
  private int fireInsurance;
  @Schema(description = "備考", example = "特になし")
  private String other;
  @Schema(description = "削除フラグ", example = "false")
  private boolean isDeleted;

}
