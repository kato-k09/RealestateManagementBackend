package com.katok09.realestate.management.data;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "不動産建物情報")
@Getter
@Setter
public class Building {

  @Schema(description = "建物ID DB登録時に自動採番されます。", example = "99")
  private int id;
  @Schema(description = "プロジェクトID プロジェクトごとに紐づけをします。", example = "99")
  private int projectId;
  @Schema(description = "建物価格", example = "1000000")
  private int buildingPrice;
  @Schema(description = "建物種別", example = "アパート")
  private String buildingType;
  @Schema(description = "建物構造", example = "鉄筋コンクリート")
  private String buildingStructure;
  @Schema(description = "建物面積", example = "100.11")
  private double buildingSize;
  @Schema(description = "建物築年", example = "2020-01-01")
  private LocalDate buildingDate;
  @Schema(description = "建物備考", example = "特になし")
  private String buildingRemark;
  @Schema(description = "削除フラグ", example = "false")
  private boolean isDeleted;

}
