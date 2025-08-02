package com.katok09.realestate.management.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "不動産土地情報")
@Getter
@Setter
public class Parcel {

  @Schema(description = "土地ID DB登録時に自動採番されます。", example = "99")
  private int id;
  @Schema(description = "プロジェクトID プロジェクトごとに紐づけをします。", example = "99")
  private int projectId;
  @Schema(description = "ユーザーID", example = "99")
  private Long userId;
  @Schema(description = "土地価格", example = "1000000")
  private int parcelPrice;
  @Schema(description = "土地住所", example = "東京都千代田区1-1-1")
  private String parcelAddress;
  @Schema(description = "土地種別", example = "宅地")
  private String parcelCategory;
  @Schema(description = "土地面積", example = "100.11")
  private double parcelSize;
  @Schema(description = "土地備考", example = "特になし")
  private String parcelRemark;
  @Schema(description = "削除フラグ", example = "false")
  private boolean isDeleted;

}
