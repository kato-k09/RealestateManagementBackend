package com.katok09.realestate.management.domain;

import com.katok09.realestate.management.data.Building;
import com.katok09.realestate.management.data.IncomeAndExpenses;
import com.katok09.realestate.management.data.Parcel;
import com.katok09.realestate.management.data.Project;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "不動産詳細情報")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RealestateDetail {

  @Schema(description = "不動産プロジェクト情報")
  private Project project;
  @Schema(description = "不動産土地情報")
  private Parcel parcel;
  @Schema(description = "不動産建物情報")
  private Building building;
  @Schema(description = "不動産収支情報")
  private IncomeAndExpenses incomeAndExpenses;
}
