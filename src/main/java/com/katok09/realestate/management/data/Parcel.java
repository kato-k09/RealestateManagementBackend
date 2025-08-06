package com.katok09.realestate.management.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
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
  private int userId;
  @Schema(description = "土地価格", example = "1000000")
  @Min(value = 0L, message = "土地価格は0円以上で入力してください。")
  @Max(value = 1000000000000000L, message = "土地価格は1,000兆円以下で入力してください。")
  private long parcelPrice;
  @Schema(description = "土地住所", example = "東京都千代田区1-1-1")
  @Size(max = 100, message = "土地住所は100文字以下で入力してください。")
  private String parcelAddress;
  @Schema(description = "土地種別", example = "宅地")
  @Size(max = 50, message = "土地種別は50文字以下で入力してください。")
  private String parcelCategory;
  @Schema(description = "土地面積", example = "100.11")
  @Min(value = 0, message = "土地面積は0㎡以上で入力してください。")
  @Max(value = 1000000000, message = "土地面積は10億㎡以下で入力してください。")
  private double parcelSize;
  @Schema(description = "土地備考", example = "特になし")
  @Size(max = 100, message = "土地備考は100文字以下で入力してください。")
  private String parcelRemark;
  @Schema(description = "削除フラグ", example = "false")
  private boolean isDeleted;

}
